import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import LandingPage from "../src/app/(landing)/page"; // Adjust path as necessary
import { useUser } from "@auth0/nextjs-auth0/client";
import { useRouter } from "next/navigation";

// Adjusting the mock setup for useRouter
const mockPush = jest.fn();
jest.mock("next/navigation", () => ({
  useRouter: () => ({
    push: mockPush,
  }),
}));

jest.mock("@auth0/nextjs-auth0/client", () => ({
  useUser: jest.fn(),
}));
jest.mock("@/components/button", () => (props) => (
  <button {...props}>{props.children}</button>
));
jest.mock("@/components/loading", () => () => <div>Loading...</div>);

describe("LandingPage", () => {
  beforeEach(() => {
    // Clear all mocks before each test
    mockPush.mockClear();
    useUser.mockClear();
  });

  it("should render the login button when not loading and user not authenticated", () => {
    useUser.mockReturnValue({ user: null, isLoading: false });
    render(<LandingPage />);
    expect(screen.getByText("Login")).toBeInTheDocument();
  });

  it("should display loading when isLoading is true", () => {
    useUser.mockReturnValue({ isLoading: true });
    render(<LandingPage />);
    expect(screen.getByText("Loading...")).toBeInTheDocument();
  });

  it("should navigate to home after login", () => {
    useUser.mockReturnValue({ user: null, isLoading: false });
    render(<LandingPage />);
    fireEvent.click(screen.getByText("Login"));
    expect(mockPush).toHaveBeenCalledWith("/api/auth/login?returnTo=/home");
  });
});
