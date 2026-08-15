import { useMapEvents } from "react-leaflet";

function MapClickHandler({ onMapClick }) {

  useMapEvents({

    click: (event) => {

      const latitude =
        event.latlng.lat;

      const longitude =
        event.latlng.lng;

      // =========================
      // TEMPORARY DEBUG OUTPUT
      // =========================
      console.log(
        "Clicked coordinates:",
        latitude,
        longitude
      );

      onMapClick({
        latitude,
        longitude
      });
    }
  });

  return null;
}

export default MapClickHandler;