import { handleAuth, handleCallback } from '@auth0/nextjs-auth0';
import { NextApiResponse, type NextApiRequest } from 'next';

export const GET = handleAuth({
  async callback(request: NextApiRequest, response: NextApiResponse) {
    try {
      return await handleCallback(request, response)
    } catch (error: any) {
      return response.redirect('/');
    }
  }
});