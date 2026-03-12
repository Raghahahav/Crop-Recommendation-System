/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        leaf: {
          50: '#edfdf1',
          100: '#d6fbe1',
          500: '#2f9e44',
          600: '#2b8a3e',
          700: '#237032',
        },
        earth: {
          100: '#f5efe6',
          300: '#e4d5c1',
          700: '#8b5e34',
        },
      },
      boxShadow: {
        soft: '0 8px 30px rgba(35, 112, 50, 0.12)',
      },
      backgroundImage: {
        grid: 'radial-gradient(circle at 1px 1px, rgba(47,158,68,0.15) 1px, transparent 0)',
      },
    },
  },
  plugins: [],
}
