import { getSession } from "@auth0/nextjs-auth0/edge";
import { Configuration, UserControllerApi, CategoryControllerApi, TimestampControllerApi, IncidentControllerApi, DaySheetControllerApi, SystemControllerApi,RatingControllerApi } from "./compassClient";

export async function getMiddleWareControllerApi() {
  const session = await getSession();
  const config = new Configuration({
    basePath: process.env.API_BASE_PATH || "http://localhost:8080/api",
    headers: {
      Authorization: `Bearer ${session?.accessToken}`
    }
  });
  return new UserControllerApi(config);
}
function getApiConfiguration() {
  const config = new Configuration({
      basePath: "/api/proxy",
  });
  return config;
}

export function getDaySheetControllerApi() {
  const config = getApiConfiguration();
  return new DaySheetControllerApi(config);
}

export function getUserControllerApi() {
  const config = getApiConfiguration();
  return new UserControllerApi(config);
}

export function getCategoryControllerApi() {
  const config = getApiConfiguration();
  return new CategoryControllerApi(config);
}
export function getRatingControllerApi() {
  const config = getApiConfiguration();
  return new RatingControllerApi(config);
}

export function getTimestampControllerApi() {
  const config = getApiConfiguration();
  return new TimestampControllerApi(config);
}

export function getIncidentControllerApi() {
  const config = getApiConfiguration();
  return new IncidentControllerApi(config);
}

export function getSystemControllerApi() {
  const config = getApiConfiguration();
  return new SystemControllerApi(config);
}