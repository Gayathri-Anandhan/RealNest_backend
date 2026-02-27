const express = require("express");
const mysql = require("mysql2");
const cors = require("cors");

const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// MySQL connection
const db = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "root123",  
  database: "realnest"
});

// Check connection
db.connect((err) => {
  if (err) {
    console.log("Database connection failed:", err);
  } else {
    console.log("Connected to MySQL");
  }
});

// API to add property
app.post("/add-property", (req, res) => {
  const { title, description, location, price, type } = req.body;

  const sql = `
    INSERT INTO properties (title, description, location, price, type)
    VALUES (?, ?, ?, ?, ?)
  `;

  db.query(sql, [title, description, location, price, type], (err, result) => {
    if (err) {
      console.log(err);
      res.status(500).json({ error: "Database error" });
    } else {
      res.json({ message: "Property added successfully!" });
    }
  });
});

// Start server
app.listen(5000, () => {
  console.log("Server running on port 5000");
});