# Smart Crop Recommendation System

A full-stack web application to recommend the best crop using soil and weather conditions.

## Project Structure

- `backend/` → Spring Boot REST API + Spark MLlib model integration
- `frontend/` → React (Vite) + Tailwind CSS dashboard UI
- `crop_data.csv` → training dataset used by backend Spark model

## Backend (Spring Boot + Spark)

### Features

- `POST /recommend` endpoint
- Validates input payload
- Trains a Spark MLlib RandomForest model at startup
- Predicts crop and returns:

```json
{
  "recommended_crop": "Rice"
}
```

### Run Backend

From project root:

```bash
mvn -f backend/pom.xml -DskipTests compile
mvn -f backend/pom.xml -DskipTests spring-boot:run
```

Backend runs at: `http://localhost:8080`

### Java Version Note

- Recommended for Spark RandomForest path: **Java 17**
- On newer JDKs (for example Java 25), Spark may fail with `getSubject is not supported`.
- In that case, the app automatically falls back to a built-in KNN predictor so `/recommend` still works.

## Frontend (React + Tailwind)

### Features

- Modern responsive dashboard UI
- Agricultural green palette and card layout
- Tooltips, icons, validation, smooth animations
- Loading state while waiting for prediction

### Run Frontend

From project root:

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at: `http://localhost:5173`

## Quick Start (Run Everything)

Open two terminals from project root.

### Terminal 1 (Backend)

```bash
mvn -f backend/pom.xml -DskipTests spring-boot:run
```

### Terminal 2 (Frontend)

```bash
cd frontend
npm run dev
```

Then open `http://localhost:5173`.

## API Request Example

`POST http://localhost:8080/recommend`

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

Quick test with curl:

```bash
curl -X POST "http://localhost:8080/recommend" \
  -H "Content-Type: application/json" \
  -d '{"N":90,"P":40,"K":40,"temperature":21,"humidity":80,"ph":6.5,"rainfall":200}'
```

## Notes

- Backend automatically searches for `crop_data.csv` in:
  1. `backend/src/main/resources/`
  2. `backend/`
  3. project root (`../crop_data.csv`)
- Current setup is local development friendly (`SparkSession` uses `local[*]`).
