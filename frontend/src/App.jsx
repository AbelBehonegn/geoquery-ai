import { useEffect, useState } from "react";
import {
  GeoJSON,
  MapContainer,
  TileLayer
} from "react-leaflet";

import "leaflet/dist/leaflet.css";

// =========================
// CUSTOM COMPONENTS
// =========================
import DrawControl from "./components/DrawControl.jsx";
import EditParcelForm from "./components/EditParcelForm.jsx";
import FitBounds from "./components/FitBounds.jsx";
import MapClickHandler from "./components/MapClickHandler.jsx";
import NearbySearch from "./components/NearbySearch.jsx";
import ParcelForm from "./components/ParcelForm.jsx";

function App() {

  // =========================
  // STATE
  // =========================

  // All parcels from backend as GeoJSON
  const [parcelData, setParcelData] = useState(null);

  // Nearby-search results
  const [searchResults, setSearchResults] = useState(null);

  // Newly drawn polygon
  const [drawnBoundary, setDrawnBoundary] = useState(null);

  // Parcel selected by clicking an existing feature
  const [selectedParcel, setSelectedParcel] = useState(null);

  // Parcel identified by clicking anywhere on the map
  const [clickedParcel, setClickedParcel] = useState(null);

  // Error message
  const [error, setError] = useState("");

  // Tracks parcel creation
  const [isSaving, setIsSaving] = useState(false);

  // Controls attribute edit form
  const [isEditing, setIsEditing] = useState(false);


  // =========================
  // LOAD DATA ON START
  // =========================
  useEffect(() => {
    loadParcelData();
  }, []);


  // =========================
  // LOAD ALL PARCEL GEOJSON
  // =========================
  function loadParcelData() {

    fetch("http://localhost:8080/api/parcels/geojson")
      .then((response) => {

        if (!response.ok) {
          throw new Error(
            "Could not load parcel data."
          );
        }

        return response.json();
      })
      .then((data) => {

        setParcelData(data);
        setError("");
      })
      .catch((fetchError) => {

        setError(fetchError.message);
      });
  }


  // =========================
  // NEARBY SEARCH
  // =========================
  function handleNearbySearch({
    latitude,
    longitude,
    distance
  }) {

    const url =
      `http://localhost:8080/api/parcels/nearby` +
      `?latitude=${latitude}` +
      `&longitude=${longitude}` +
      `&distance=${distance}`;

    fetch(url)
      .then((response) => {

        if (!response.ok) {
          throw new Error(
            "Nearby search failed."
          );
        }

        return response.json();
      })
      .then((parcels) => {

        const featureCollection = {
          type: "FeatureCollection",

          features: parcels
            .filter(
              (parcel) =>
                parcel.latitude != null &&
                parcel.longitude != null
            )
            .map((parcel) => ({

              type: "Feature",

              geometry: {
                type: "Point",

                coordinates: [
                  parcel.longitude,
                  parcel.latitude
                ]
              },

              properties: {
                id: parcel.id,
                ownerName: parcel.ownerName,
                address: parcel.address,
                area: parcel.area
              }
            }))
        };

        setSearchResults(featureCollection);
        setError("");
      })
      .catch((searchError) => {

        setError(searchError.message);
      });
  }


  // =========================
  // MAP CLICK IDENTIFICATION
  // =========================
  function handleMapClick({
    latitude,
    longitude
  }) {

    const url =
      `http://localhost:8080/api/parcels/contains` +
      `?latitude=${latitude}` +
      `&longitude=${longitude}`;

    fetch(url)
      .then((response) => {

        // No parcel contains this point
        if (response.status === 404) {

          setClickedParcel(null);

          return null;
        }

        if (!response.ok) {

          throw new Error(
            "Could not identify parcel."
          );
        }

        return response.json();
      })
      .then((parcel) => {

        if (parcel) {
          setClickedParcel(parcel);
        }

        setError("");
      })
      .catch((clickError) => {

        setError(clickError.message);
      });
  }


  // =========================
  // NEW POLYGON DRAWN
  // =========================
  function handlePolygonCreated(coordinates) {

    setDrawnBoundary(coordinates);
    setError("");
  }


  // =========================
  // SAVE NEW PARCEL
  // =========================
  function handleSaveParcel(parcelRequest) {

    setIsSaving(true);

    fetch(
      "http://localhost:8080/api/parcels",
      {
        method: "POST",

        headers: {
          "Content-Type": "application/json"
        },

        body: JSON.stringify(parcelRequest)
      }
    )
      .then((response) => {

        if (!response.ok) {

          throw new Error(
            "Could not save parcel."
          );
        }

        return response.json();
      })
      .then(() => {

        return fetch(
          "http://localhost:8080/api/parcels/geojson"
        );
      })
      .then((response) => {

        if (!response.ok) {

          throw new Error(
            "Parcel saved, but map refresh failed."
          );
        }

        return response.json();
      })
      .then((data) => {

        setParcelData(data);

        setDrawnBoundary(null);
        setSearchResults(null);
        setSelectedParcel(null);
        setClickedParcel(null);

        setError("");
      })
      .catch((saveError) => {

        setError(saveError.message);
      })
      .finally(() => {

        setIsSaving(false);
      });
  }


  // =========================
  // UPDATE PARCEL ATTRIBUTES
  // =========================
  function handleUpdateParcel(updatedParcel) {

    fetch(
      `http://localhost:8080/api/parcels/${updatedParcel.id}`,
      {
        method: "PUT",

        headers: {
          "Content-Type": "application/json"
        },

        body: JSON.stringify({
          ownerName: updatedParcel.ownerName,
          address: updatedParcel.address,
          area: updatedParcel.area
        })
      }
    )
      .then((response) => {

        if (!response.ok) {

          throw new Error(
            "Could not update parcel."
          );
        }

        return response.json();
      })
      .then(() => {

        return fetch(
          "http://localhost:8080/api/parcels/geojson"
        );
      })
      .then((response) => {

        if (!response.ok) {

          throw new Error(
            "Parcel updated, but map refresh failed."
          );
        }

        return response.json();
      })
      .then((data) => {

        setParcelData(data);

        setSelectedParcel(null);
        setClickedParcel(null);

        setIsEditing(false);
        setSearchResults(null);

        setError("");
      })
      .catch((updateError) => {

        setError(updateError.message);
      });
  }


  // =========================
  // UPDATE PARCEL GEOMETRY
  // =========================
  function handlePolygonEdited({
    id,
    coordinates
  }) {

    if (!id) {

      setError(
        "Could not identify the parcel being edited."
      );

      return;
    }

    fetch(
      `http://localhost:8080/api/parcels/${id}/boundary`,
      {
        method: "PUT",

        headers: {
          "Content-Type": "application/json"
        },

        body: JSON.stringify({
          boundary: coordinates
        })
      }
    )
      .then((response) => {

        if (!response.ok) {

          throw new Error(
            "Could not update parcel boundary."
          );
        }

        return response.json();
      })
      .then(() => {

        return fetch(
          "http://localhost:8080/api/parcels/geojson"
        );
      })
      .then((response) => {

        if (!response.ok) {

          throw new Error(
            "Boundary updated, but map refresh failed."
          );
        }

        return response.json();
      })
      .then((data) => {

        setParcelData(data);

        setSelectedParcel(null);
        setClickedParcel(null);
        setSearchResults(null);

        setError("");
      })
      .catch((geometryError) => {

        setError(
          geometryError.message
        );
      });
  }


  // =========================
  // DELETE PARCEL
  // =========================
  function handleDeleteParcel() {

    if (!selectedParcel?.id) {
      return;
    }

    const confirmed =
      window.confirm(
        `Delete parcel ${selectedParcel.id} - ${
          selectedParcel.ownerName ??
          "Unknown owner"
        }?`
      );

    if (!confirmed) {
      return;
    }

    fetch(
      `http://localhost:8080/api/parcels/${selectedParcel.id}`,
      {
        method: "DELETE"
      }
    )
      .then((response) => {

        if (!response.ok) {

          throw new Error(
            "Could not delete parcel."
          );
        }

        setSelectedParcel(null);
        setClickedParcel(null);
        setSearchResults(null);
        setIsEditing(false);

        return fetch(
          "http://localhost:8080/api/parcels/geojson"
        );
      })
      .then((response) => {

        if (!response.ok) {

          throw new Error(
            "Parcel deleted, but map refresh failed."
          );
        }

        return response.json();
      })
      .then((data) => {

        setParcelData(data);
        setError("");
      })
      .catch((deleteError) => {

        setError(deleteError.message);
      });
  }


  // =========================
  // MAIN PARCEL STYLE
  // =========================
  function parcelStyle(feature) {

    if (
      feature.geometry?.type !==
      "Polygon"
    ) {
      return {};
    }

    const isClickedParcel =
      clickedParcel?.id ===
      feature.properties?.id;


    // Highlight identified parcel
    if (isClickedParcel) {

      return {
        weight: 5,
        fillOpacity: 0.7
      };
    }


    return {
      weight: 2,
      fillOpacity: 0.35
    };
  }


  // =========================
  // NEARBY RESULT STYLE
  // =========================
  function nearbyResultStyle(feature) {

    if (
      feature.geometry?.type ===
      "Polygon"
    ) {

      return {
        weight: 4,
        fillOpacity: 0.6
      };
    }

    return {};
  }


  // =========================
  // CONFIGURE EACH PARCEL
  // =========================
  function onEachParcel(
    feature,
    layer
  ) {

    const properties =
      feature.properties ?? {};


    // =========================
    // POPUP
    // =========================
    layer.bindPopup(`
      <strong>
        ${properties.ownerName ?? "Unknown owner"}
      </strong>

      <br/>

      ${properties.address ?? "No address"}

      <br/>

      Area:
      ${
        properties.area != null
          ? `${Number(properties.area).toFixed(2)} m²`
          : "Not available"
      }

      <br/>

      Parcel ID:
      ${properties.id ?? "Unknown"}
    `);


    // =========================
    // SELECT PARCEL
    // =========================
    layer.on({

      click: (event) => {

        setSelectedParcel(properties);

        setIsEditing(false);

        event.target.openPopup();
      }
    });


    // =========================
    // POLYGON HOVER
    // =========================
    if (
      feature.geometry?.type ===
      "Polygon"
    ) {

      layer.on({

        mouseover: (event) => {

          event.target.setStyle({
            weight: 4,
            fillOpacity: 0.55
          });
        },

        mouseout: (event) => {

          event.target.setStyle({
            weight: 2,
            fillOpacity: 0.35
          });
        }
      });
    }
  }


  // =========================
  // USER INTERFACE
  // =========================
  return (

    <div>


      {/* =========================
          NEARBY SEARCH PANEL
         ========================= */}
      <NearbySearch
        onSearch={handleNearbySearch}
      />


      {/* =========================
          MAP CLICK RESULT
         ========================= */}
      {clickedParcel && (

        <div
          style={{
            position: "absolute",
            bottom: "16px",
            right: "16px",
            zIndex: 1200,
            width: "240px",
            background: "white",
            padding: "12px",
            borderRadius: "8px",
            boxShadow:
              "0 2px 10px rgba(0,0,0,0.25)"
          }}
        >

          <h3 style={{ marginTop: 0 }}>
            Parcel Found
          </h3>


          <div>
            <strong>ID:</strong>{" "}
            {clickedParcel.id}
          </div>


          <div>
            <strong>Owner:</strong>{" "}
            {clickedParcel.ownerName ??
              "Unknown"}
          </div>


          <div>
            <strong>Address:</strong>{" "}
            {clickedParcel.address ??
              "No address"}
          </div>


          {/* =========================
              FORMATTED AREA
             ========================= */}
          <div>
            <strong>Area:</strong>{" "}

            {clickedParcel.area != null
              ? `${Number(
                  clickedParcel.area
                ).toFixed(2)} m²`
              : "Not available"}
          </div>


          <button
            type="button"
            onClick={() =>
              setClickedParcel(null)
            }
            style={{
              width: "100%",
              marginTop: "10px",
              padding: "8px",
              cursor: "pointer"
            }}
          >
            Close
          </button>

        </div>
      )}


      {/* =========================
          SELECTED PARCEL PANEL
         ========================= */}
      {selectedParcel && (

        <div
          style={{
            position: "absolute",
            top: "190px",
            left: "16px",
            zIndex: 1200,
            width: "220px",
            background: "white",
            padding: "12px",
            borderRadius: "8px",
            boxShadow:
              "0 2px 10px rgba(0,0,0,0.25)"
          }}
        >

          <h3 style={{ marginTop: 0 }}>
            Selected Parcel
          </h3>


          <div>
            <strong>ID:</strong>{" "}
            {selectedParcel.id}
          </div>


          <div>
            <strong>Owner:</strong>{" "}
            {selectedParcel.ownerName ??
              "Unknown"}
          </div>


          <div>
            <strong>Address:</strong>{" "}
            {selectedParcel.address ??
              "No address"}
          </div>


          {/* =========================
              FORMATTED AREA
             ========================= */}
          <div>
            <strong>Area:</strong>{" "}

            {selectedParcel.area != null
              ? `${Number(
                  selectedParcel.area
                ).toFixed(2)} m²`
              : "Not available"}
          </div>


          {/* EDIT PARCEL */}
          <button
            type="button"
            onClick={() =>
              setIsEditing(true)
            }
            style={{
              width: "100%",
              marginTop: "12px",
              padding: "8px",
              cursor: "pointer"
            }}
          >
            Edit Parcel
          </button>


          {/* DELETE PARCEL */}
          <button
            type="button"
            onClick={
              handleDeleteParcel
            }
            style={{
              width: "100%",
              marginTop: "8px",
              padding: "8px",
              cursor: "pointer"
            }}
          >
            Delete Parcel
          </button>


          {/* CLOSE */}
          <button
            type="button"
            onClick={() => {

              setSelectedParcel(null);

              setIsEditing(false);
            }}
            style={{
              width: "100%",
              marginTop: "8px",
              padding: "8px",
              cursor: "pointer"
            }}
          >
            Close
          </button>

        </div>
      )}


      {/* =========================
          ATTRIBUTE EDIT FORM
         ========================= */}
      {isEditing &&
        selectedParcel && (

          <EditParcelForm
            parcel={
              selectedParcel
            }

            onSave={
              handleUpdateParcel
            }

            onCancel={() =>
              setIsEditing(false)
            }
          />
        )}


      {/* =========================
          CREATE PARCEL FORM
         ========================= */}
      {drawnBoundary && (

        <ParcelForm
          boundary={
            drawnBoundary
          }

          onSave={
            handleSaveParcel
          }

          onCancel={() =>
            setDrawnBoundary(null)
          }
        />
      )}


      {/* =========================
          SAVING MESSAGE
         ========================= */}
      {isSaving && (

        <div
          style={{
            position: "absolute",
            bottom: "16px",
            left: "16px",
            zIndex: 1200,
            background: "white",
            padding: "10px 14px",
            borderRadius: "6px",
            boxShadow:
              "0 2px 10px rgba(0,0,0,0.25)"
          }}
        >
          Saving parcel...
        </div>
      )}


      {/* =========================
          ERROR MESSAGE
         ========================= */}
      {error && (

        <div
          style={{
            position: "absolute",
            top: "16px",
            left: "50%",
            transform:
              "translateX(-50%)",
            zIndex: 1300,
            background: "white",
            padding: "10px 14px",
            borderRadius: "6px",
            boxShadow:
              "0 2px 10px rgba(0,0,0,0.25)"
          }}
        >
          {error}
        </div>
      )}


      {/* =========================
          LEAFLET MAP
         ========================= */}
      <MapContainer
        center={[
          39.084,
          -77.1528
        ]}
        zoom={16}
        style={{
          height: "100vh",
          width: "100%"
        }}
      >


        {/* OPENSTREETMAP */}
        <TileLayer
          attribution=
            "&copy; OpenStreetMap contributors"
          url=
            "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />


        {/* MAP CLICK IDENTIFICATION */}
        <MapClickHandler
          onMapClick={
            handleMapClick
          }
        />


        {/* DRAW + GEOMETRY EDIT */}
        <DrawControl
          parcelData={
            parcelData
          }
          onPolygonCreated={
            handlePolygonCreated
          }
          onPolygonEdited={
            handlePolygonEdited
          }
        />


        {/* AUTO-ZOOM */}
        <FitBounds
          geoJson={
            parcelData
          }
        />


        {/* =========================
            MAIN PARCEL GEOJSON
           ========================= */}
        {parcelData && (

          <GeoJSON
            key={JSON.stringify({
              parcelData,
              clickedParcelId:
                clickedParcel?.id
            })}
            data={
              parcelData
            }
            style={
              parcelStyle
            }
            onEachFeature={
              onEachParcel
            }
          />
        )}


        {/* =========================
            NEARBY RESULTS
           ========================= */}
        {searchResults && (

          <>

            <FitBounds
              geoJson={
                searchResults
              }
            />

            <GeoJSON
              key={
                JSON.stringify(
                  searchResults
                )
              }
              data={
                searchResults
              }
              style={
                nearbyResultStyle
              }
              onEachFeature={
                onEachParcel
              }
            />

          </>
        )}

      </MapContainer>

    </div>
  );
}

export default App;