import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const API = process.env.REACT_APP_API_URL;

function StudentLeaveForm() {
  const navigate = useNavigate();
  const token = localStorage.getItem("auth");
  if (!token) navigate("/");

  const [form, setForm] = useState({
    name: "", regNo: "", year: "", email: "", fromDate: "", toDate: ""
  });
  const [msg, setMsg] = useState(null);

  const handleChange = e => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setMsg(null);
    try {
      const res = await axios.post(`${API}/leave/request`, form, {
        headers: { Authorization: `Basic ${token}` }
      });
      setMsg({ type: "success", text: "Request submitted." });
      setForm({ name: "", regNo: "", year: "", email: "", fromDate: "", toDate: "" });
    } catch (err) {
      const data = err.response?.data;
      if (typeof data === "string") {
        setMsg({ type: "error", text: data });
      } else if (typeof data === "object" && data !== null) {
        const message = data.message || JSON.stringify(data);
        setMsg({ type: "error", text: message });
      } else {
        setMsg({ type: "error", text: err.message });
      }
    }
  };

  const logout = () => {
    localStorage.removeItem("auth");
    navigate("/");
  };

  return (
    <div className="container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Student Leave Request</h2>
        <div>
          <button onClick={() => navigate("/")}>Home</button>
          <button onClick={logout} style={{ marginLeft: 8 }}>Logout</button>
        </div>
      </div>

      <form className="form" onSubmit={submit}>
        <input
          name="name"
          placeholder="Name"
          value={form.name}
          onChange={handleChange}
          required
        />
        <input
          name="regNo"
          placeholder="Reg No"
          value={form.regNo}
          onChange={handleChange}
          required
        />
        <input
          name="year"
          placeholder="Year"
          value={form.year}
          onChange={handleChange}
          required
        />
        <input
          name="email"
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
          required
        />
        <label>From Date</label>
        <input
          name="fromDate"
          type="date"
          value={form.fromDate}
          onChange={handleChange}
          required
        />
        <label>To Date</label>
        <input
          name="toDate"
          type="date"
          value={form.toDate}
          onChange={handleChange}
          required
        />
        <button className="primary" type="submit">Submit</button>
      </form>

      {msg && (
        <div
          className={`message ${msg.type === "error" ? "error" : "success"}`}
          style={{ marginTop: 12 }}
        >
          {msg.text}
        </div>
      )}
    </div>
  );
}

export default StudentLeaveForm;
