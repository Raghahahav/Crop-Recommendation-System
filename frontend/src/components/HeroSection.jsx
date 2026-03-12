import { motion } from 'framer-motion'
import { Sprout } from 'lucide-react'

export default function HeroSection() {
  return (
    <motion.section
      initial={{ opacity: 0, y: -18 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="relative overflow-hidden rounded-3xl border border-leaf-100 bg-white/80 p-8 shadow-soft backdrop-blur"
    >
      <div className="absolute inset-0 -z-10 bg-grid bg-grid opacity-40" />
      <div className="flex items-center gap-3 text-leaf-700">
        <div className="rounded-full bg-leaf-100 p-2">
          <Sprout className="h-6 w-6" />
        </div>
        <span className="text-sm font-semibold tracking-wide uppercase">Smart Agriculture</span>
      </div>

      <h1 className="mt-4 text-3xl font-bold leading-tight text-slate-900 md:text-5xl">
        Crop Recommendation BDA LAB
      </h1>
      <p className="mt-3 max-w-2xl text-base text-slate-600 md:text-lg">
        Helping farmers choose the best crop for their soil
      </p>
    </motion.section>
  )
}
