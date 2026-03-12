export const cropInfo = {
  Rice: 'Rice grows best in warm and humid conditions with adequate rainfall and fertile soil.',
  Maize: 'Maize is adaptable and performs well in moderate rainfall with balanced nutrients.',
  Chickpea: 'Chickpea prefers cooler and drier conditions with well-drained soils.',
  Kidneybeans: 'Kidney beans need fertile soil and moderate temperature with controlled moisture.',
  Pigeonpeas: 'Pigeon pea is drought tolerant and suitable for semi-arid conditions.',
  Mothbeans: 'Moth bean performs well in low-moisture environments and light soils.',
  Mungbean: 'Mung bean grows quickly in warm climates with moderate rainfall.',
  Blackgram: 'Black gram prefers humid weather and loamy, nutrient-rich soils.',
  Lentil: 'Lentil is suitable for cooler seasons and moderate soil fertility.',
  Pomegranate: 'Pomegranate thrives in warm climates with low to moderate humidity.',
  Banana: 'Banana needs high humidity, warm temperatures, and nutrient-rich soil.',
}

export function getCropDescription(name) {
  return cropInfo[name] || 'This crop matches your farm conditions based on current model analysis.'
}
