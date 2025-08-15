import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const API = process.env.REACT_APP_API_URL;

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [msg, setMsg] = useState(null);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setMsg(null);
    try {
      const auth = { username, password };
      const token = btoa(`${username}:${password}`);
      const res = await axios.get(`${API}/auth/role`, {
        headers: { Authorization: `Basic ${token}` }
      });
      if (res.data && res.data.roles) {
        const roles = res.data.roles;
        if (roles.includes("ROLE_ADMIN")) {
          localStorage.setItem("auth", token);
          navigate("/admin");
        } else if (roles.includes("ROLE_STUDENT")) {
          localStorage.setItem("auth", token);
          navigate("/student");
        } else {
          setMsg({ type: "error", text: "No valid role found." });
        }
      } else {
        setMsg({ type: "error", text: "Unable to authenticate." });
      }
    } catch (err) {
      setMsg({ type: "error", text: "Login failed. Check username/password." });
    }
  };

  return (
    <div className="container">
      <h2>Leave Management - Login</h2>
      <form className="form" onSubmit={handleLogin}>
        <input value={username} onChange={e=>setUsername(e.target.value)} placeholder="Username" required />
        <input type="password" value={password} onChange={e=>setPassword(e.target.value)} placeholder="Password" required />
        <button className="primary" type="submit">Login</button>
      </form>
      {msg && <div className={`message ${msg.type === 'error' ? 'error' : 'success'}`}>{msg.text}</div>}
      <p style={{marginTop:12}}>
        Use admin/admin123 for Admin, student1..student5 / student123 for Students.
      </p>
    </div>
  );
}

export default LoginPage;
