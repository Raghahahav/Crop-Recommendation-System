import { useMemo, useState } from 'react'
import { AlertTriangle } from 'lucide-react'
import HeroSection from './components/HeroSection'
import RecommendationForm from './components/RecommendationForm'
import ResultCard from './components/ResultCard'
import LoadingOverlay from './components/LoadingOverlay'
import { getRecommendation } from './lib/api'

const initialValues = {
  N: 90,
  P: 40,
  K: 40,
  temperature: 21,
  humidity: 80,
  ph: 6.5,
  rainfall: 200,
}

export default function App() {
  const [values, setValues] = useState(initialValues)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [crop, setCrop] = useState('')

  const hasInvalidValue = useMemo(() => {
    return Object.values(values).some((v) => Number.isNaN(Number(v)) || v === '' || v === null)
  }, [values])

  const handleChange = (e) => {
    const { name, value } = e.target
    setValues((prev) => ({ ...prev, [name]: value === '' ? '' : Number(value) }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    if (hasInvalidValue) {
      setError('Please enter valid numeric values for all fields.')
      return
    }

    try {
      setLoading(true)
      const response = await getRecommendation(values)
      setCrop(response.recommended_crop || '')
      if (!response.recommended_crop) {
        setError('No recommendation received. Please try again.')
      }
    } catch (err) {
      const apiMessage = err?.response?.data?.message
      const fieldErrors = err?.response?.data?.errors
      let message = apiMessage || 'Unable to fetch recommendation. Please check server connection.'

      if (fieldErrors && typeof fieldErrors === 'object') {
        const details = Object.entries(fieldErrors)
          .map(([field, msg]) => `${field}: ${msg}`)
          .join(' | ')
        if (details) {
          message = `${message} (${details})`
        }
      }

      setError(message)
      setCrop('')
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="mx-auto max-w-6xl px-4 py-8 md:px-6 md:py-10">
      <div className="grid gap-6">
        <HeroSection />

        <section className="relative">
          <RecommendationForm values={values} onChange={handleChange} onSubmit={handleSubmit} loading={loading} />
          {loading ? <LoadingOverlay /> : null}
        </section>

        {error ? (
          <div className="rounded-2xl border border-red-200 bg-red-50 p-4 text-red-700 shadow-sm">
            <p className="flex items-center gap-2 text-sm font-medium">
              <AlertTriangle className="h-4 w-4" />
              {error}
            </p>
          </div>
        ) : null}

        <ResultCard crop={crop} />
      </div>
    </main>
  )
}
