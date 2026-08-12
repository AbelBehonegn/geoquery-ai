import { useMapEvents } from "react-leaflet";

function MapClickHandler({ onMapClick }) {

  // =========================
  // LISTEN FOR MAP CLICKS
  // =========================
  useMapEvents({

    click: (event) => {

      // Leaflet gives us:
      // event.latlng.lat
      // event.latlng.lng

      const latitude =
        event.latlng.lat;

      const longitude =
        event.latlng.lng;

      // Send coordinates back
      // to App.jsx
      onMapClick({
        latitude,
        longitude
      });
    }
  });


  // This component does not
  // display any HTML.
  return null;
}

export default MapClickHandler;