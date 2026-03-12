import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  timeout: 30000,
})

export async function getRecommendation(payload) {
  const { data } = await api.post('/recommend', payload)
  return data
}
