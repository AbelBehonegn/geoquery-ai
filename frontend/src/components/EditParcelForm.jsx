import { useState } from "react";

function EditParcelForm({
  parcel,
  onSave,
  onCancel
}) {

  // =========================
  // FORM STATE
  // =========================
  const [ownerName, setOwnerName] =
    useState(parcel.ownerName ?? "");

  const [address, setAddress] =
    useState(parcel.address ?? "");


  // =========================
  // SUBMIT UPDATED ATTRIBUTES
  // =========================
  function handleSubmit(event) {

    event.preventDefault();

    onSave({
      id: parcel.id,
      ownerName,
      address,

      // Keep existing calculated area.
      // Geometry edits will cause
      // PostGIS to recalculate it.
      area: parcel.area
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
        top: "190px",
        left: "260px",
        zIndex: 1300,
        width: "260px",
        background: "white",
        padding: "14px",
        borderRadius: "8px",
        boxShadow:
          "0 2px 12px rgba(0,0,0,0.3)"
      }}
    >

      <h3 style={{ marginTop: 0 }}>
        Edit Parcel
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


      {/* DISPLAY CALCULATED AREA */}
      <div
        style={{
          marginBottom: "12px"
        }}
      >
        <strong>
          Area:
        </strong>{" "}
        {parcel.area != null
          ? `${parcel.area.toFixed(2)} m²`
          : "Not available"}
      </div>


      {/* SAVE */}
      <button
        type="submit"
        style={{
          width: "100%",
          padding: "8px"
        }}
      >
        Save Changes
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

export default EditParcelForm;