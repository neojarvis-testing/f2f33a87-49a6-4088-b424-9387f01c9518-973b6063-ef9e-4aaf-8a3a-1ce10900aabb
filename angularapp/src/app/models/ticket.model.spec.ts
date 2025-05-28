import { Ticket } from "./ticket.model";

describe('Ticket Model', () => {

  fit('Frontend_Ticket_model_should_create_an_instance_with_defined_properties', () => {
    // Create a sample Ticket object
    const ticket: Ticket = {
      ticketId: 1,
      title: 'Login Issue',
      description: 'Unable to login to the portal using valid credentials.',
      priority: 'High',
      status: 'Open',
      createdDate: new Date('2023-12-01'),
      resolutionDate: new Date('2023-12-05'),
      issueCategory: 'Technical',
      resolutionSummary: 'Password reset and provided access to the portal.',
      userId: 101,
      agentId: 202,
      satisfied: true
    };

    expect(ticket).toBeTruthy();
    expect(ticket.ticketId).toBe(1);
    expect(ticket.title).toBe('Login Issue');
    expect(ticket.description).toBe('Unable to login to the portal using valid credentials.');
    expect(ticket.priority).toBe('High');
    expect(ticket.status).toBe('Open');
    expect(ticket.createdDate).toEqual(new Date('2023-12-01'));
    expect(ticket.resolutionDate).toEqual(new Date('2023-12-05'));
    expect(ticket.issueCategory).toBe('Technical');
    expect(ticket.resolutionSummary).toBe('Password reset and provided access to the portal.');
    expect(ticket.userId).toBe(101);
    expect(ticket.agentId).toBe(202);
    expect(ticket.satisfied).toBe(true);
  });

});
