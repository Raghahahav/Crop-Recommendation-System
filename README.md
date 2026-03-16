# Smart Crop Recommendation System

An end-to-end java+spark based agriculture platform that recommends suitable crops based on soil nutrients and environmental conditions.

The solution includes:

- A modern React dashboard for farmers
- A Spring Boot REST API for prediction requests
- Spark MLlib integration for RandomForest-based recommendations
- Automatic fallback prediction path for newer Java runtimes

---

## Key Features

- Clean, responsive frontend built with React + Vite + TailwindCSS
- Farm input form with validation, tooltips, icons, and loading states
- REST API endpoint for crop recommendation
- Machine learning prediction using 7 parameters:
  - Nitrogen (`N`)
  - Phosphorus (`P`)
  - Potassium (`K`)
  - Temperature
  - Humidity
  - Soil pH
  - Rainfall
- Spark RandomForest model training at startup (when runtime supports it)
- Safe fallback to KNN predictor if Spark is not fully compatible with current JDK

---

## Repository Structure

```text
CropRecommendationSpark/
├── backend/                  # Spring Boot API + ML service
├── frontend/                 # React UI (Vite + Tailwind)
├── crop_data.csv             # Training dataset
└── README.md
```

---

## System Workflow (File-wise)

| Step | Workflow Stage             | Primary File(s)                                                                                  | What Happens                                                                                     | Output                                   |
| ---- | -------------------------- | ------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------ | ---------------------------------------- |
| 1    | User input collection      | `frontend/src/components/RecommendationForm.jsx`                                                 | Farmer enters N, P, K, temperature, humidity, pH, rainfall via validated form inputs.            | Structured input values in UI state      |
| 2    | Client validation + submit | `frontend/src/App.jsx`                                                                           | Frontend validates numeric values and sends POST request to backend API.                         | JSON request to `/recommend`             |
| 3    | API call transport         | `frontend/src/lib/api.js`                                                                        | Axios client sends payload to backend base URL.                                                  | HTTP request/response handling           |
| 4    | Request validation         | `backend/src/main/java/com/smartcrop/recommendation/model/RecommendationRequest.java`            | Spring validates required fields and value ranges.                                               | Valid request object or validation error |
| 5    | Endpoint orchestration     | `backend/src/main/java/com/smartcrop/recommendation/controller/RecommendationController.java`    | Controller receives request, calls service, formats success/error responses.                     | `recommended_crop` response JSON         |
| 6    | ML prediction service      | `backend/src/main/java/com/smartcrop/recommendation/service/SparkCropRecommendationService.java` | Service selects Spark RandomForest path or fallback KNN path depending on runtime compatibility. | Predicted crop label                     |
| 7    | Dataset source             | `crop_data.csv`                                                                                  | Historical labeled crop data used for model training / fallback nearest-neighbor lookup.         | Prediction knowledge base                |
| 8    | Result rendering           | `frontend/src/components/ResultCard.jsx`                                                         | UI displays recommended crop and description.                                                    | Farmer-friendly recommendation card      |

---

## Recommendation Algorithm (Based on Files)

| Algorithm Layer              | File                                                                                             | Implementation Summary                                                                                                              | Runtime Behavior                                                      |
| ---------------------------- | ------------------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------- |
| Feature set definition       | `backend/src/main/java/com/smartcrop/recommendation/service/SparkCropRecommendationService.java` | Uses 7 agronomic features: `N`, `P`, `K`, `temperature`, `humidity`, `ph`, `rainfall`.                                              | Common for both Spark and fallback paths                              |
| Primary model: RandomForest  | `backend/src/main/java/com/smartcrop/recommendation/service/SparkCropRecommendationService.java` | Builds Spark `VectorAssembler`, applies `StringIndexer`, trains `RandomForestClassifier`, predicts class index, maps to crop label. | Used when Spark initializes successfully                              |
| Fallback model: KNN voting   | `backend/src/main/java/com/smartcrop/recommendation/service/SparkCropRecommendationService.java` | Loads `crop_data.csv`, computes Euclidean distance to all samples, takes top-k neighbors, uses inverse-distance weighted vote.      | Used when Spark path is unavailable (e.g., newer JDK incompatibility) |
| Request contract enforcement | `backend/src/main/java/com/smartcrop/recommendation/model/RecommendationRequest.java`            | Ensures all required inputs are present and within bounds before prediction logic executes.                                         | Prevents invalid data from entering model pipeline                    |
| API response contract        | `backend/src/main/java/com/smartcrop/recommendation/model/RecommendationResponse.java`           | Returns standardized response key: `recommended_crop`.                                                                              | Keeps frontend/backend integration consistent                         |
| User-visible interpretation  | `frontend/src/lib/cropInfo.js` and `frontend/src/components/ResultCard.jsx`                      | Maps predicted crop to a short explanatory description for user readability.                                                        | Converts raw prediction into actionable recommendation                |

---

## Technology Stack

### Frontend

- React
- Vite
- Tailwind CSS
- Framer Motion
- Axios

### Backend

- Java 17+
- Spring Boot
- Apache Spark MLlib (RandomForest)
- Maven

---

## Prerequisites

- Node.js 18+
- npm 9+
- Java 17+ (Java 17 recommended for full Spark path)
- Maven 3.9+

---

## Local Setup & Run

### 1) Clone and move into project

```bash
git clone <your-repository-url>
cd CropRecommendationSpark
```

### 2) Run backend

```bash
mvn -f backend/pom.xml -DskipTests compile
mvn -f backend/pom.xml -DskipTests spring-boot:run
```

Backend URL: `http://localhost:8080`

### 3) Run frontend (new terminal)

```bash
cd frontend
npm install
npm run dev
```

Frontend URL: `http://localhost:5173`

---

## API Contract

### Endpoint

`POST /recommend`

### Request Body

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

### Success Response

```json
{
  "recommended_crop": "Rice"
}
```

### Example cURL

```bash
curl -X POST "http://localhost:8080/recommend" \
  -H "Content-Type: application/json" \
  -d '{"N":90,"P":40,"K":40,"temperature":21,"humidity":80,"ph":6.5,"rainfall":200}'
```

---

## Runtime Compatibility Notes

- **Recommended runtime:** Java 17 for Spark RandomForest flow.
- On newer JDKs (for example Java 25), Spark/Hadoop may throw `getSubject is not supported`.
- The backend is designed to continue serving predictions through a fallback KNN predictor, so API uptime is preserved.

---

## Troubleshooting

### Frontend shows “Validation failed”

- Ensure all 7 fields are numeric and present.
- Backend now returns field-level validation details; check the exact message in the UI.

### Backend not reachable (`curl` exit code 7)

- Confirm backend process is running.
- Verify port 8080 is available.
- Re-run backend command from repository root.

### Spark startup warnings on macOS / newer JDK

- This is expected on some JDK versions.
- Fallback predictor will still provide recommendations.

---

## Future Enhancements

- Persist and version trained ML models
- Add authentication and user history
- Deploy backend + frontend with Docker
- Add analytics dashboard for model confidence and trends

---

## License

This project is for educational and demonstration use. Add a formal LICENSE file if you plan public distribution.
