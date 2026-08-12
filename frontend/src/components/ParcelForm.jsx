import { useState } from "react";

function ParcelForm({
  boundary,
  onSave,
  onCancel
}) {

  // =========================
  // FORM STATE
  // =========================
  const [ownerName, setOwnerName] = useState("");
  const [address, setAddress] = useState("");


  // =========================
  // SUBMIT NEW PARCEL
  // =========================
  function handleSubmit(event) {

    event.preventDefault();

    if (!boundary || boundary.length < 4) {
      return;
    }


    // =========================
    // CALCULATE CENTER POINT
    // =========================
    // Used only for parcel location.
    // Area is calculated by PostGIS.
    const longitude =
      boundary.reduce(
        (sum, coordinate) =>
          sum + coordinate[0],
        0
      ) / boundary.length;

    const latitude =
      boundary.reduce(
        (sum, coordinate) =>
          sum + coordinate[1],
        0
      ) / boundary.length;


    // =========================
    // SEND REQUEST TO APP.JSX
    // =========================
    onSave({
      ownerName,
      address,

      // Temporary value.
      // Backend will replace this
      // with PostGIS calculated area.
      area: 0,

      latitude,
      longitude,
      boundary
    });
  }


  // =========================
  // FORM UI
  // =========================
  return (

    <form
      onSubmit={handleSubmit}
      style={{
        position: "absolute",
        top: "16px",
        right: "70px",
        zIndex: 1200,
        width: "260px",
        background: "white",
        padding: "14px",
        borderRadius: "8px",
        boxShadow:
          "0 2px 12px rgba(0,0,0,0.3)"
      }}
    >

      <h3 style={{ marginTop: 0 }}>
        New Parcel
      </h3>


      {/* OWNER */}
      <label>
        Owner name

        <input
          type="text"
          value={ownerName}
          onChange={(event) =>
            setOwnerName(
              event.target.value
            )
          }
          required
          style={{
            width: "100%",
            marginBottom: "10px"
          }}
        />
      </label>


      {/* ADDRESS */}
      <label>
        Address

        <input
          type="text"
          value={address}
          onChange={(event) =>
            setAddress(
              event.target.value
            )
          }
          required
          style={{
            width: "100%",
            marginBottom: "12px"
          }}
        />
      </label>


      {/* SAVE */}
      <button
        type="submit"
        style={{
          width: "100%",
          padding: "8px"
        }}
      >
        Save Parcel
      </button>


      {/* CANCEL */}
      <button
        type="button"
        onClick={onCancel}
        style={{
          width: "100%",
          marginTop: "8px",
          padding: "8px"
        }}
      >
        Cancel
      </button>

    </form>
  );
}

export default ParcelForm;