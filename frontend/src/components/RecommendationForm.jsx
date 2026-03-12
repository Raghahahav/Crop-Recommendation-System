import { motion } from 'framer-motion'
import { Beaker, CloudRain, Droplets, FlaskConical, Thermometer, Trees, Waves } from 'lucide-react'
import ParameterInput from './ParameterInput'

const fields = [
  { key: 'N', label: 'Nitrogen (N)', unit: '', min: 0, max: 200, step: 1, hint: 'Essential nutrient for leaf growth.', icon: Beaker },
  { key: 'P', label: 'Phosphorus (P)', unit: '', min: 0, max: 200, step: 1, hint: 'Supports root and flower development.', icon: FlaskConical },
  { key: 'K', label: 'Potassium (K)', unit: '', min: 0, max: 200, step: 1, hint: 'Improves overall plant health and disease resistance.', icon: Trees },
  { key: 'temperature', label: 'Temperature', unit: '°C', min: -10, max: 60, step: 0.1, hint: 'Average farm temperature.', icon: Thermometer },
  { key: 'humidity', label: 'Humidity', unit: '%', min: 0, max: 100, step: 0.1, hint: 'Relative air moisture percentage.', icon: Droplets },
  { key: 'ph', label: 'Soil pH', unit: '', min: 0, max: 14, step: 0.1, hint: 'Soil acidity/alkalinity scale.', icon: Waves },
  { key: 'rainfall', label: 'Rainfall', unit: 'mm', min: 0, max: 500, step: 0.1, hint: 'Expected rainfall amount.', icon: CloudRain },
]

export default function RecommendationForm({ values, onChange, onSubmit, loading }) {
  return (
    <motion.form
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.45 }}
      onSubmit={onSubmit}
      className="relative rounded-3xl border border-slate-200 bg-white p-6 shadow-soft"
    >
      <h2 className="mb-4 text-xl font-semibold text-slate-800">Soil Condition Inputs</h2>

      <div className="grid gap-4 sm:grid-cols-2">
        {fields.map((f) => (
          <ParameterInput
            key={f.key}
            icon={f.icon}
            label={f.label}
            name={f.key}
            unit={f.unit}
            min={f.min}
            max={f.max}
            step={f.step}
            hint={f.hint}
            value={values[f.key]}
            onChange={onChange}
          />
        ))}
      </div>

      <button
        type="submit"
        disabled={loading}
        className="mt-6 w-full rounded-2xl bg-gradient-to-r from-leaf-600 to-leaf-500 px-5 py-3 text-base font-semibold text-white shadow-md transition hover:brightness-110 disabled:cursor-not-allowed disabled:opacity-70"
      >
        {loading ? 'Predicting...' : 'Get Crop Recommendation'}
      </button>
    </motion.form>
  )
}
