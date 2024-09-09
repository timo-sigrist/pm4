import React, { useState, useEffect } from 'react';

interface SliderProps {
  min: number;
  max: number;
  onChange: (value: number) => void;
  name: string;
}

const Slider: React.FC<SliderProps> = ({ min, max, onChange, name }) => {
  const [sliderValue, setSliderValue] = useState(0);
  const steps = max - min + 1;

  const handleClick = (index: number) => {
    setSliderValue(index + min);
    onChange(index + min);
  };

  return (
    <div className="slider-container flex items-center">
      <span className="mr-2">{min === 0 && max === 1 ? "Nein" : min}</span>
      <div className="slider flex-grow flex justify-between items-center relative">
        {[...Array(steps)].map((_, index) => (
          <div
            key={index}
            className={`slider-step w-4 h-4 rounded-full z-40 cursor-pointer ${sliderValue === index + min ? 'bg-black' : 'bg-gray-400'}`}
            onClick={() => handleClick(index)}
            style={{ left: `${(index / (steps - 1)) * 100}%` }}
          />
        ))}
        <div className="slider-line absolute w-full h-1 bg-gray-400"></div>
      </div>
      <span className="mr-2">{min === 0 && max === 1 ? "Ja" : max}</span>
      <input type="hidden" name={name} />
    </div>
  );
};

export default Slider;
