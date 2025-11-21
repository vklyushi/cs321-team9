import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import RecordsTable from "../RecordsTable/RecordsTable.jsx";

const TITLES = {
    allergies: "Allergies",
    bloodwork: "Bloodwork",
    vaccines: "Immunizations",
    insurancerecords: "Insurance Records",
    insuranceproviders: "Insurance Providers",
    procedures: "Procedures",
    injuries: "Injuries",
    medications: "Medications",
    medical_history: "Medical History",
    family_history: "Family History",
    calendar: "Calendar Events"
};

export default function RecordsPage() {
    const { category } = useParams();
    const [rows, setRows] = useState([]);
    const [loading, setLoading] = useState(true);

    const userid = Number(localStorage.getItem("userid"));

    // ---------- RELOAD FUNCTION ----------
    async function reload() {
        setLoading(true);
        try {
            const res = await fetch(
                `http://localhost:8080/api/records?category=${category}&userid=${userid}`
            );
            const json = await res.json();
            setRows(Array.isArray(json) ? json : []);
        } catch (e) {
            console.error(e);
            setRows([]);
        }
        setLoading(false);
    }

    // Load when category changes
    useEffect(() => {
        reload();
    }, [category]);


    return (
        <div style={{ padding: "16px" }}>
            <h2 style={{ marginBottom: "12px" }}>
                {TITLES[category] || category}
            </h2>

            <Link to={`/app/addRecords?category=${category}`}>
                <button
                    style={{
                        padding: "8px 14px",
                        background: "#9DCAC1",
                        border: "1px solid #888",
                        borderRadius: "6px",
                        cursor: "pointer",
                        marginBottom: "12px"
                    }}
                >
                    Add New
                </button>
            </Link>

            {loading ? (
                <div>Loading...</div>
            ) : rows.length === 0 ? (
                <div>No records found.</div>
            ) : (
                <RecordsTable rows={rows} reload={reload} />
            )}
        </div>
    );
}
