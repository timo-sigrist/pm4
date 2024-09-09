import { getSession } from "@auth0/nextjs-auth0";
import { NextRequest } from "next/server";

async function callBackend(request: NextRequest) {
  const session = await getSession();
  const pathname = request.nextUrl.pathname.split("proxy")[1];
  const requestUrl = process.env.APP_URL ? `https://${process.env.APP_URL}/api${pathname}` : `http://localhost:8080/api${pathname}`;

  const body = await request.text();
  const headers = Object.assign(Object.fromEntries(request.headers.entries()), {
    "Authorization": `Bearer ${session && session.accessToken}`,
    "Content-Length": body ? Buffer.byteLength(body) : 0,
  });

  return fetch(requestUrl, {
    method: request.method,
    headers: headers,
    body: body ? body : undefined,
  }).then(response => {
    return response
  }).catch(() => {
    return new Response("Internal Server Error", { status: 500 });
  });
}

export async function GET(request: NextRequest) {
  return callBackend(request);
}

export async function POST(request: NextRequest) {
  return callBackend(request);
}

export async function PUT(request: NextRequest) {
  return callBackend(request);
} 

export async function PATCH(request: NextRequest) {
  return callBackend(request);
}

export async function DELETE(request: NextRequest) {
  return callBackend(request);
}

export async function OPTIONS(request: NextRequest) {
  return callBackend(request);
}