import { useEffect, useRef } from "react";
import { useMap } from "react-leaflet";
import L from "leaflet";

import "leaflet-draw";
import "leaflet-draw/dist/leaflet.draw.css";

function DrawControl({
  parcelData,
  onPolygonCreated,
  onPolygonEdited
}) {

  // =========================
  // ACCESS THE LEAFLET MAP
  // =========================
  const map = useMap();


  // =========================
  // KEEP LATEST CALLBACKS
  // WITHOUT RECREATING
  // LEAFLET DRAW
  // =========================
  const createdCallbackRef =
    useRef(onPolygonCreated);

  const editedCallbackRef =
    useRef(onPolygonEdited);


  // Update callback references whenever
  // React gives us newer functions.
  useEffect(() => {
    createdCallbackRef.current =
      onPolygonCreated;
  }, [onPolygonCreated]);


  useEffect(() => {
    editedCallbackRef.current =
      onPolygonEdited;
  }, [onPolygonEdited]);


  // =========================
  // LEAFLET FEATURE GROUP
  // =========================
  // Leaflet Draw requires its own
  // FeatureGroup containing the layers
  // that it is allowed to edit.
  const drawnItemsRef = useRef(null);


  // =========================
  // CREATE DRAW CONTROL ONCE
  // =========================
  useEffect(() => {

    const drawnItems =
      new L.FeatureGroup();

    drawnItemsRef.current =
      drawnItems;

    map.addLayer(drawnItems);


    // =========================
    // DRAW TOOL CONFIGURATION
    // =========================
    const drawControl =
      new L.Control.Draw({

        position: "topright",

        draw: {

          // Allow Polygon drawing
          polygon: true,

          // Disable tools
          // we don't currently need
          rectangle: false,
          circle: false,
          circlemarker: false,
          marker: false,
          polyline: false
        },

        edit: {

          // Only layers inside this
          // FeatureGroup are editable
          featureGroup: drawnItems,

          edit: {},

          // We already have our own
          // parcel delete button
          remove: false
        }
      });


    map.addControl(drawControl);


    // =========================
    // NEW POLYGON CREATED
    // =========================
    function handleCreated(event) {

      const layer = event.layer;

      // Keep newly drawn polygon
      // inside the editable group.
      drawnItems.addLayer(layer);

      const geoJson =
        layer.toGeoJSON();

      if (
        geoJson.geometry.type ===
        "Polygon"
      ) {

        createdCallbackRef.current?.(
          geoJson.geometry.coordinates[0]
        );
      }
    }


    // =========================
    // EXISTING POLYGON EDITED
    // =========================
    function handleEdited(event) {

      event.layers.eachLayer(
        (layer) => {

          const geoJson =
            layer.toGeoJSON();

          if (
            geoJson.geometry.type ===
            "Polygon"
          ) {

            // The ID was attached when
            // we created the editable copy.
            const parcelId =
              layer.options.parcelId;

            editedCallbackRef.current?.({
              id: parcelId,
              coordinates:
                geoJson.geometry
                  .coordinates[0]
            });
          }
        }
      );
    }


    // =========================
    // REGISTER LEAFLET EVENTS
    // =========================
    map.on(
      L.Draw.Event.CREATED,
      handleCreated
    );

    map.on(
      L.Draw.Event.EDITED,
      handleEdited
    );


    // =========================
    // CLEANUP
    // =========================
    return () => {

      map.off(
        L.Draw.Event.CREATED,
        handleCreated
      );

      map.off(
        L.Draw.Event.EDITED,
        handleEdited
      );

      map.removeControl(drawControl);

      map.removeLayer(drawnItems);
    };

  }, [map]);


  // =========================
  // COPY SAVED POSTGIS
  // POLYGONS INTO THE
  // EDITABLE FEATURE GROUP
  // =========================
  useEffect(() => {

    const drawnItems =
      drawnItemsRef.current;

    if (
      !drawnItems ||
      !parcelData?.features
    ) {
      return;
    }


    // IMPORTANT:
    // These are COPIES.
    //
    // We are no longer moving the
    // original React GeoJSON layers
    // into Leaflet Draw.
    //
    // This fixes the disappearing
    // polygon problem.
    drawnItems.clearLayers();


    parcelData.features.forEach(
      (feature) => {

        // Only polygon parcels
        // should be editable.
        if (
          feature.geometry?.type !==
          "Polygon"
        ) {
          return;
        }


        // =========================
        // CREATE EDITABLE COPY
        // =========================
        const geoJsonLayer =
          L.geoJSON(feature);


        geoJsonLayer.eachLayer(
          (layer) => {

            // Store parcel ID directly
            // on this editable copy.
            layer.options.parcelId =
              feature.properties?.id;

            drawnItems.addLayer(layer);
          }
        );
      }
    );

  }, [parcelData]);


  // This component renders no HTML.
  // It works directly with Leaflet.
  return null;
}

export default DrawControl;