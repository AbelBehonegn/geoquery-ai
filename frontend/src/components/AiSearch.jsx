import { useState } from "react";

function AiSearch({
  onQuery,
  resultMessage
}) {

  // =========================
  // FORM STATE
  // =========================
  const [query, setQuery] =
    useState("");

  const [loading, setLoading] =
    useState(false);


  // =========================
  // SUBMIT AI QUERY
  // =========================
  async function handleSubmit(event) {

    event.preventDefault();

    if (!query.trim()) {
      return;
    }

    setLoading(true);

    try {

      await onQuery(query);

    } finally {

      setLoading(false);
    }
  }


  // =========================
  // UI
  // =========================
  return (

    <div
      style={{
        position: "absolute",
        top: "16px",
        left: "400px",
        zIndex: 1400,
        width: "360px",
        background: "white",
        padding: "12px",
        borderRadius: "8px",
        boxShadow:
          "0 2px 10px rgba(0,0,0,0.25)"
      }}
    >

      <strong>
        GeoQueryAI
      </strong>


      <form
        onSubmit={handleSubmit}
      >

        <input
          type="text"
          value={query}
          onChange={(event) =>
            setQuery(
              event.target.value
            )
          }
          placeholder=
            "Find parcels within 500 meters of 39.084, -77.1528"
          style={{
            width: "100%",
            marginTop: "8px",
            padding: "8px",
            boxSizing: "border-box"
          }}
        />


        <button
          type="submit"
          disabled={loading}
          style={{
            width: "100%",
            marginTop: "8px",
            padding: "8px",
            cursor: "pointer"
          }}
        >
          {loading
            ? "Thinking..."
            : "Ask AI"}
        </button>

      </form>


      {resultMessage && (

        <div
          style={{
            marginTop: "8px",
            fontSize: "14px"
          }}
        >
          {resultMessage}
        </div>
      )}

    </div>
  );
}

export default AiSearch;