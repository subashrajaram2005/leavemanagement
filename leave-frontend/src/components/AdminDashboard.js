import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const API = process.env.REACT_APP_API_URL;

function AdminDashboard() {
  const navigate = useNavigate();
  const token = localStorage.getItem("auth");

  // Redirect if no token - use effect to avoid side effects during render
  useEffect(() => {
    if (!token) {
      navigate("/");
    }
  }, [token, navigate]);

  const [requests, setRequests] = useState([]);
  const [msg, setMsg] = useState(null);

  const load = async () => {
    setMsg(null);
    try {
      const res = await axios.get(`${API}/leave/requests`, {
        headers: { Authorization: `Basic ${token}` },
      });
      setRequests(res.data);
    } catch (err) {
      setMsg({ type: "error", text: "Failed to load requests" });
    }
  };

  useEffect(() => {
    if (token) load();
  }, [token]);

  // Helper to safely parse error messages
  const getErrorText = (error) => {
    if (!error) return "Unknown error";
    if (typeof error === "string") return error;
    if (error.message) return error.message;
    if (typeof error === "object") {
      if (error.error) return error.error;
      if (error.status && error.path && error.timestamp) {
        return `Error ${error.status} at ${error.path}`;
      }
      return JSON.stringify(error);
    }
    return String(error);
  };

  const action = async (id, act) => {
    try {
      let url = `${API}/leave/${id}/${act}`;
      if (act === "delete") url = `${API}/leave/${id}`;
      const config = { headers: { Authorization: `Basic ${token}` } };
      let res;
      if (act === "delete") res = await axios.delete(url, config);
      else res = await axios.put(url, {}, config);
      setMsg({ type: "success", text: `${act} succeeded` });
      load();
    } catch (err) {
      const errorText = err.response?.data
        ? getErrorText(err.response.data)
        : err.message;
      setMsg({ type: "error", text: errorText });
    }
  };

  const logout = () => {
    localStorage.removeItem("auth");
    navigate("/");
  };

  return (
    <div className="container">
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <h2>Admin Dashboard</h2>
        <div>
          <button onClick={() => navigate("/")}>Home</button>
          <button onClick={logout} style={{ marginLeft: 8 }}>
            Logout
          </button>
        </div>
      </div>

      {msg && (
        <div
          className={`message ${msg.type === "error" ? "error" : "success"}`}
          style={{ marginBottom: 10 }}
        >
          {msg.text}
        </div>
      )}

      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>RegNo</th>
            <th>Year</th>
            <th>Email</th>
            <th>From</th>
            <th>To</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {requests.length === 0 ? (
            <tr>
              <td colSpan="8" style={{ textAlign: "center" }}>
                No leave requests found.
              </td>
            </tr>
          ) : (
            requests.map((r) => (
              <tr key={r.id}>
                <td>{r.name}</td>
                <td>{r.regNo}</td>
                <td>{r.year}</td>
                <td>{r.email}</td>
                <td>{r.fromDate}</td>
                <td>{r.toDate}</td>
                <td>{r.status}</td>
                <td className="actions">
                  <button
                    disabled={r.status !== "Pending"}
                    onClick={() => action(r.id, "approve")}
                  >
                    Approve
                  </button>
                  <button
                    disabled={r.status !== "Pending"}
                    onClick={() => action(r.id, "reject")}
                  >
                    Reject
                  </button>
                  <button
                    disabled={r.status !== "Pending"}
                    onClick={() => action(r.id, "delete")}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

export default AdminDashboard;
