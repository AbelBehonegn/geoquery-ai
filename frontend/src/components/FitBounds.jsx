import { useEffect } from "react";
import { useMap } from "react-leaflet";
import L from "leaflet";

function FitBounds({ geoJson }) {
  const map = useMap();

  useEffect(() => {
    if (!geoJson) {
      return;
    }

    const layer = L.geoJSON(geoJson);
    const bounds = layer.getBounds();

    if (bounds.isValid()) {
      map.fitBounds(bounds, {
        padding: [40, 40]
      });
    }
  }, [geoJson, map]);

  return null;
}

export default FitBounds;