import { useState } from "react";

function NearbySearch({ onSearch }) {
  const [latitude, setLatitude] = useState("39.084");
  const [longitude, setLongitude] = useState("-77.1528");
  const [distance, setDistance] = useState("500");

  function handleSubmit(event) {
    event.preventDefault();

    onSearch({
      latitude: Number(latitude),
      longitude: Number(longitude),
      distance: Number(distance)
    });
  }

  return (
    <form
      onSubmit={handleSubmit}
      style={{
        position: "absolute",
        top: "16px",
        left: "16px",
        zIndex: 1000,
        background: "white",
        padding: "12px",
        borderRadius: "8px",
        boxShadow: "0 2px 10px rgba(0,0,0,0.25)",
        width: "220px"
      }}
    >
      <h3 style={{ marginTop: 0 }}>Nearby Parcels</h3>

      <label>
        Latitude
        <input
          type="number"
          step="any"
          value={latitude}
          onChange={(event) => setLatitude(event.target.value)}
          style={{ width: "100%", marginBottom: "8px" }}
        />
      </label>

      <label>
        Longitude
        <input
          type="number"
          step="any"
          value={longitude}
          onChange={(event) => setLongitude(event.target.value)}
          style={{ width: "100%", marginBottom: "8px" }}
        />
      </label>

      <label>
        Distance in meters
        <input
          type="number"
          min="1"
          value={distance}
          onChange={(event) => setDistance(event.target.value)}
          style={{ width: "100%", marginBottom: "10px" }}
        />
      </label>

      <button type="submit" style={{ width: "100%" }}>
        Search
      </button>
    </form>
  );
}

export default NearbySearch;