import type { Config } from 'tailwindcss';

export default {
  content: ['./src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      backgroundImage: {
        'welcome': "url('https://stadtmuur.ch/wp-content/uploads/2023/11/cropped-header-scaled-3.jpg')",
      },
    }
  },
  plugins: [],
} satisfies Config;
