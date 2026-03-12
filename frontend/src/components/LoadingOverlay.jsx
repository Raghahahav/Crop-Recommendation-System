import { motion } from 'framer-motion'

export default function LoadingOverlay() {
  return (
    <div className="absolute inset-0 z-20 grid place-items-center rounded-3xl bg-white/70 backdrop-blur-sm">
      <div className="flex flex-col items-center gap-3">
        <motion.div
          className="h-10 w-10 rounded-full border-4 border-leaf-200 border-t-leaf-600"
          animate={{ rotate: 360 }}
          transition={{ repeat: Infinity, duration: 0.9, ease: 'linear' }}
        />
        <p className="text-sm font-medium text-slate-700">Analyzing soil and weather conditions...</p>
      </div>
    </div>
  )
}
