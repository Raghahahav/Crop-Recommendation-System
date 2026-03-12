import { motion } from 'framer-motion'
import { Leaf } from 'lucide-react'
import { getCropDescription } from '../lib/cropInfo'

export default function ResultCard({ crop }) {
  if (!crop) return null

  return (
    <motion.section
      initial={{ opacity: 0, y: 14 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.35 }}
      className="rounded-3xl border border-leaf-200 bg-gradient-to-br from-white to-leaf-50 p-6 shadow-soft"
    >
      <h2 className="mb-4 text-lg font-semibold text-slate-800">Recommended Crop</h2>
      <div className="flex items-start gap-4">
        <div className="rounded-2xl bg-leaf-100 p-3">
          <Leaf className="h-6 w-6 text-leaf-700" />
        </div>
        <div>
          <p className="text-2xl font-bold text-leaf-700">{crop}</p>
          <p className="mt-2 text-sm text-slate-600">{getCropDescription(crop)}</p>
        </div>
      </div>
    </motion.section>
  )
}
