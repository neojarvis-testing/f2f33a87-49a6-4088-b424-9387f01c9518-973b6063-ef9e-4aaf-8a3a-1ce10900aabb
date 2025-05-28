import { SupportAgent } from "./support-agent.model";

describe('SupportAgent Model', () => {

  fit('Frontend_SupportAgent_model_should_create_an_instance_with_defined_properties', () => {
    // Create a sample SupportAgent object
    const supportAgent: SupportAgent = {
      agentId: 1,
      name: 'Alice Johnson',
      email: 'alice.johnson@example.com',
      phone: '9876543210',
      expertise: 'Networking',
      experience: '5 years',
      status: 'Available',
      addedDate: new Date('2023-01-15'),
      profile: 'base64stringofimage',
      shiftTiming: '9 AM - 6 PM',
      remarks: 'Handles critical issues efficiently'
    };

    expect(supportAgent).toBeTruthy();
    expect(supportAgent.agentId).toBe(1);
    expect(supportAgent.name).toBe('Alice Johnson');
    expect(supportAgent.email).toBe('alice.johnson@example.com');
    expect(supportAgent.phone).toBe('9876543210');
    expect(supportAgent.expertise).toBe('Networking');
    expect(supportAgent.experience).toBe('5 years');
    expect(supportAgent.status).toBe('Available');
    expect(supportAgent.addedDate).toEqual(new Date('2023-01-15'));
    expect(supportAgent.profile).toBe('base64stringofimage');
    expect(supportAgent.shiftTiming).toBe('9 AM - 6 PM');
    expect(supportAgent.remarks).toBe('Handles critical issues efficiently');
  });

});
