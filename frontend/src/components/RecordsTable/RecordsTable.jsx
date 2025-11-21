import React from "react";

export default function RecordsTable({ rows, reload }) {
    const userid = Number(localStorage.getItem("userid"));

    async function handleDelete(category, id) {
        if (!window.confirm("Are you sure you want to delete this record?")) {
            return;
        }

        try {
            const res = await fetch(
                `http://localhost:8080/api/delete?category=${category}&id=${id}&userid=${userid}`,
                { method: "DELETE" }
            );

            if (res.ok) {
                alert("Record deleted.");
                reload();   // Refresh table
            } else {
                alert("Failed to delete record.");
            }
        } catch (err) {
            console.error("Delete failed:", err);
            alert("Error deleting record.");
        }
    }

    return (
        <table
            style={{
                width: "100%",
                borderCollapse: "collapse",
                background: "white",
                borderRadius: "6px",
                overflow: "hidden",
            }}
        >
            <thead>
                <tr style={{ background: "#9DCAC1", textAlign: "left" }}>
                    <th style={{ padding: "10px" }}>Name</th>
                    <th style={{ padding: "10px" }}>Date</th>
                    <th style={{ padding: "10px" }}>Notes</th>
                    <th style={{ padding: "10px" }}>Actions</th>
                </tr>
            </thead>

            <tbody>
                {rows.map((r) => (
                    <tr key={r.id} style={{ borderBottom: "1px solid #eee" }}>
                        <td style={{ padding: "10px" }}>{r.name}</td>
                        <td style={{ padding: "10px" }}>{r.date}</td>
                        <td style={{ padding: "10px" }}>
                            {r.notes && r.notes.trim().length > 0
                                ? r.notes
                                : <span style={{ color: "#888" }}>None</span>}
                        </td>

                        <td style={{ padding: "10px" }}>
                            <button
                                onClick={() => handleDelete(r.category, r.id)}
                                style={{
                                    padding: "6px 10px",
                                    background: "#d9534f",
                                    color: "white",
                                    border: "none",
                                    borderRadius: "4px",
                                    cursor: "pointer",
                                }}
                            >
                                Delete
                            </button>
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
}
