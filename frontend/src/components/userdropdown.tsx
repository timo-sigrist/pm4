// UserDropdown.tsx

import React, { useState } from 'react';
import {UserDto} from "@/openapi/compassClient";
import {noop} from "@babel/types";

interface UserDropdownProps {
    options: UserDto[];
    onSelect: (selectedOption: UserDto) => void; // Function to handle option selection
}

const UserDropdown: React.FC<UserDropdownProps> = ({ options, onSelect }) => {
    const [selectedOption, setSelectedOption] = useState<UserDto | null>(null);
    const [isDropdownOpen, setIsDropdownOpen] = useState(false); // Track dropdown visibility

    const handleSelect = (option: UserDto) => {
        setSelectedOption(option);
        setIsDropdownOpen(false); // Close dropdown after selection
        onSelect(option); // Notify parent component about the selected option
    };

    return (
        <div className="relative">
            <button
                className="bg-gray-300 text-gray-700 font-semibold py-2 px-4 rounded inline-flex items-center"
                onClick={() => setIsDropdownOpen(!isDropdownOpen)} // Toggle dropdown visibility
            >
                {selectedOption?.givenName || "Select"}
                <svg
                    className="w-4 h-4 ml-2"
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 20 20"
                    fill="currentColor"
                >
                    <path
                        fillRule="evenodd"
                        d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414zM10 4a1 1 0 110 2 1 1 0 010-2z"
                        clipRule="evenodd"
                    />
                </svg>
            </button>
            <ul
                className={`absolute left-0 mt-2 w-24 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none 
          ${isDropdownOpen ? '' : 'hidden'}`} // Use isDropdownOpen state to toggle visibility
                role="menu"
                aria-orientation="vertical"
                aria-labelledby="options-menu"
            >
                {options.map((option: UserDto, index: number) => (
                    <li
                        key={index}
                        className="py-1"
                        onClick={() => option.userId != undefined ? handleSelect(option) : noop() }
                        role="menuitem"
                    >
            <span className="block px-4 py-2 text-gray-800 hover:bg-gray-100">
              {option.givenName}
            </span>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default UserDropdown;
