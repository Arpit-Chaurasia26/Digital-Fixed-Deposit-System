import apiClient from './axios';
import {
  SupportTicket,
  SupportTicketRequest,
  SupportTicketPage,
  TicketStatus,
} from '@/types';

export const supportService = {
  // Create a new support ticket
  async createTicket(data: SupportTicketRequest): Promise<SupportTicket> {
    const response = await apiClient.post('/support', data);
    return response.data;
  },

  // Get user's own tickets
  async getMyTickets(): Promise<SupportTicket[]> {
    const response = await apiClient.get('/support/my-tickets');
    return response.data;
  },

  // Get ticket by ID (Admin)
  async getTicketById(ticketId: number): Promise<SupportTicket> {
    const response = await apiClient.get(`/support/${ticketId}`);
    console.log('getTicketById response:', response.data);
    return response.data;
  },

  // Update ticket status
  async updateTicketStatus(ticketId: number, status: TicketStatus): Promise<SupportTicket> {
    console.log('Updating ticket status:', { ticketId, status });
    try {
      // Send as plain text string (controller expects @RequestBody String)
      const response = await apiClient.patch(
        `/support/${ticketId}/status`,
        status,
        {
          headers: {
            'Content-Type': 'text/plain',
          },
        }
      );
      console.log('Status update response:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('updateTicketStatus failed:', error.response?.data || error.message);
      throw error;
    }
  },

  // Get all tickets with filters (Admin)
  async getAllTickets(params?: {
    status?: string;
    createdFrom?: string;
    createdTo?: string;
    userId?: number;
    fdId?: number;
    page?: number;
    size?: number;
  }): Promise<SupportTicketPage> {
    const queryParams = new URLSearchParams();
    if (params?.status) queryParams.append('status', params.status);
    if (params?.createdFrom) queryParams.append('createdFrom', params.createdFrom);
    if (params?.createdTo) queryParams.append('createdTo', params.createdTo);
    if (params?.userId) queryParams.append('userId', params.userId.toString());
    if (params?.fdId) queryParams.append('fdId', params.fdId.toString());
    if (params?.page !== undefined) queryParams.append('page', params.page.toString());
    if (params?.size !== undefined) queryParams.append('size', params.size.toString());

    const response = await apiClient.get(`/support?${queryParams.toString()}`);
    return response.data;
  },

  // Update ticket response (Admin or User)
  async updateTicketResponse(ticketId: number, responseText: string): Promise<SupportTicket> {
    console.log('Sending response update:', { ticketId, responseText, length: responseText.length });
    try {
      const response = await apiClient.patch(
        `/support/${ticketId}/response`,
        responseText,
        {
          headers: {
            'Content-Type': 'text/plain',
          },
        }
      );
      console.log('Update response result:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('updateTicketResponse failed:', error.response?.data || error.message);
      throw error;
    }
  },
};
