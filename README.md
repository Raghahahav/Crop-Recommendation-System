# 🌾 Smart Crop Recommendation System

An **end-to-end agriculture intelligence platform** that recommends the most suitable crop based on soil nutrients and environmental conditions.

The system combines **machine learning, distributed computing, and a modern web interface** to assist farmers in making better cultivation decisions.

The platform includes:

* ⚛️ **React Dashboard** for farmers
* ☕ **Spring Boot REST API** for prediction requests
* ⚡ **Apache Spark MLlib** for RandomForest crop prediction
* 🛡 **Automatic fallback prediction** using K-Nearest Neighbors when Spark runtime is incompatible

---

# 🚀 Key Features

* Clean **responsive dashboard** built with **React + Vite + TailwindCSS**
* Farmer-friendly **input form with validation and tooltips**
* REST API for crop prediction
* ML prediction using **7 agronomic parameters**

### Input Parameters

| Parameter       | Description            |
| --------------- | ---------------------- |
| **N**           | Nitrogen level in soil |
| **P**           | Phosphorus level       |
| **K**           | Potassium level        |
| **temperature** | Average temperature    |
| **humidity**    | Atmospheric humidity   |
| **ph**          | Soil pH level          |
| **rainfall**    | Rainfall level         |

### ML Features

* RandomForest classifier using **Spark MLlib**
* Model training during backend startup
* **KNN fallback model** for runtime compatibility
* Robust input validation and API contracts

---

# 📂 Repository Structure

```
CropRecommendationSpark/
│
├── backend/                  # Spring Boot backend + ML logic
│
├── frontend/                 # React dashboard
│
├── crop_data.csv             # Training dataset
│
└── README.md
```

---

# 🔄 System Workflow

| Step | Stage               | File                                  | Description                               |
| ---- | ------------------- | ------------------------------------- | ----------------------------------------- |
| 1    | Input Collection    | `RecommendationForm.jsx`              | Farmer enters soil and climate parameters |
| 2    | Client Validation   | `App.jsx`                             | Frontend validates numeric input          |
| 3    | API Transport       | `api.js`                              | Axios sends request to backend            |
| 4    | Request Validation  | `RecommendationRequest.java`          | Spring validates parameters               |
| 5    | Endpoint Processing | `RecommendationController.java`       | Request routed to ML service              |
| 6    | ML Prediction       | `SparkCropRecommendationService.java` | RandomForest or KNN prediction            |
| 7    | Dataset Access      | `crop_data.csv`                       | Training dataset used                     |
| 8    | Result Rendering    | `ResultCard.jsx`                      | Recommended crop displayed                |

---

# 🧠 Recommendation Algorithm

## Feature Engineering

Features used:

```
N, P, K, temperature, humidity, ph, rainfall
```

Vector creation using:

```
VectorAssembler
```

---

## Primary Model — RandomForest (Spark MLlib)

Implementation:

```
VectorAssembler
 → StringIndexer
 → RandomForestClassifier
 → Prediction
```

Used when:

```
Spark initializes successfully
```

---

## Fallback Model — KNN

If Spark runtime fails (for example **JDK incompatibility**):

```
Dataset → Distance Calculation → Top K Neighbors → Weighted Voting
```

This ensures:

✔ Prediction service never fails
✔ Backend always responds

---

# 🧰 Technology Stack

## Frontend

* React
* Vite
* TailwindCSS
* Framer Motion
* Axios

## Backend

* Java 17+
* Spring Boot
* Apache Spark MLlib
* Maven

---

# 📋 Prerequisites

Install the following tools:

| Tool         | Version    |
| ------------ | ---------- |
| Node.js      | 18+        |
| npm          | 9+         |
| Java         | 17+        |
| Maven        | 3.9+       |
| Apache Spark | 3.x or 4.x |

---

# ⚙️ Local Setup

## 1️⃣ Clone Repository

```
git clone https://github.com/Raghahahav/Crop-Recommendation-System
cd CropRecommendationSpark
```

---

# ▶️ Running the Backend

```
mvn -f backend/pom.xml -DskipTests compile
mvn -f backend/pom.xml -DskipTests spring-boot:run
```

Backend runs at:

```
http://localhost:8080
```

---

# ▶️ Running the Frontend

Open a new terminal:

```
cd frontend
npm install
npm run dev
```

Frontend runs at:

```
http://localhost:5173
```

---

# 🧪 API Contract

## Endpoint

```
POST /recommend
```

## Request

```json
{
  "N": 90,
  "P": 40,
  "K": 40,
  "temperature": 21,
  "humidity": 80,
  "ph": 6.5,
  "rainfall": 200
}
```

## Response

```json
{
  "recommended_crop": "Rice"
}
```

---

## Example cURL

```
curl -X POST "http://localhost:8080/recommend" \
-H "Content-Type: application/json" \
-d '{"N":90,"P":40,"K":40,"temperature":21,"humidity":80,"ph":6.5,"rainfall":200}'
```

---

# ⚡ Running Spark ML Code in Terminal

This section shows how to **compile and run Spark Java ML code**.

---

# 🍎 macOS Execution (Spark + MLlib)

### 1️⃣ Set Spark Path

```
export SPARK_HOME=/opt/homebrew/Cellar/apache-spark/4.1.1/libexec
```

Verify:

```
echo $SPARK_HOME
```

Output:

```
/opt/homebrew/Cellar/apache-spark/4.1.1/libexec
```

---

### 2️⃣ Go to Project Folder

```
cd ~/Desktop/CropRecommendationSpark
```

---

### 3️⃣ Compile Java Program

```
javac -cp "$SPARK_HOME/jars/*" CropRecommendation.java
```

This generates:

```
CropRecommendation.class
```

---

### 4️⃣ Package into JAR

```
jar cvf CropRecommendation.jar *.class
```

Output:

```
CropRecommendation.jar
```

---

### 5️⃣ Run with Spark

```
spark-submit --class CropRecommendation CropRecommendation.jar
```

---

# 🪟 Windows Execution (Spark + MLlib)

### 1️⃣ Set Spark Path

Open **Command Prompt**:

```
set SPARK_HOME=C:\spark
```

Verify:

```
echo %SPARK_HOME%
```

Example output:

```
C:\spark
```

---

### 2️⃣ Navigate to Project Folder

```
cd Desktop\CropRecommendationSpark
```

---

### 3️⃣ Compile Java Program

```
javac -cp "%SPARK_HOME%\jars\*" CropRecommendation.java
```

Output:

```
CropRecommendation.class
```

---

### 4️⃣ Package into JAR

```
jar cvf CropRecommendation.jar *.class
```

---

### 5️⃣ Run Spark Job

```
spark-submit --class CropRecommendation CropRecommendation.jar
```

---

# ⚠️ Runtime Compatibility Notes

Recommended runtime:

```
Java 17
```

Some newer JDK versions may produce errors like:

```
getSubject is not supported
```

If Spark fails to initialize:

✔ Backend automatically switches to **KNN fallback model**

---

# 🛠 Troubleshooting

### Frontend shows “Validation failed”

* Ensure all **7 parameters are numeric**
* Check backend validation response

---

### Backend not reachable

Check backend is running:

```
http://localhost:8080
```

If not:

```
mvn -f backend/pom.xml spring-boot:run
```

---

### Spark startup warnings

On macOS or new JDK versions you may see warnings.

This is **normal**.

The fallback predictor will still work.

---

# 🔮 Future Enhancements

* Persist trained ML models
* Add authentication for farmers
* Deploy using **Docker**
* Analytics dashboard for prediction confidence
* Mobile application support

---

# 📜 License

This project is intended for **educational and demonstration purposes**.

Add a formal `LICENSE` file if distributing publicly.
