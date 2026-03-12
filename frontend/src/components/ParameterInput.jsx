import { Info } from 'lucide-react'

export default function ParameterInput({ icon: Icon, label, name, unit, hint, min, max, step, value, onChange }) {
  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm transition hover:border-leaf-300 hover:shadow-md">
      <label htmlFor={name} className="mb-2 flex items-center justify-between text-sm font-medium text-slate-700">
        <span className="flex items-center gap-2">
          <Icon className="h-4 w-4 text-leaf-600" />
          {label}
        </span>
        <span title={hint} className="inline-flex cursor-help items-center gap-1 text-xs text-slate-500">
          <Info className="h-3.5 w-3.5" />
          Help
        </span>
      </label>

      <div className="flex items-center gap-2">
        <input
          id={name}
          name={name}
          type="number"
          min={min}
          max={max}
          step={step}
          value={value}
          onChange={onChange}
          className="w-full rounded-xl border border-slate-300 px-3 py-2 text-sm outline-none transition focus:border-leaf-500 focus:ring-2 focus:ring-leaf-100"
          required
        />
        {unit ? <span className="text-sm text-slate-500">{unit}</span> : null}
      </div>
    </div>
  )
}
