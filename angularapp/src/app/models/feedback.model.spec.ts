import { Feedback } from "./feedback.model";

describe('Feedback Model', () => {

  fit('Frontend_Feedback_model_should_create_an_instance_with_defined_properties', () => {
    // Create a sample Feedback object
    const feedback: Feedback = {
      feedbackId: 1,
      feedbackText: 'Great support and quick resolution!',
      date: new Date('2023-12-01'),
      userId: 101,
      agentId: 202,
      ticketId: 303,
      category: 'Support',
      rating: 5
    };

    expect(feedback).toBeTruthy();
    expect(feedback.feedbackId).toBe(1);
    expect(feedback.feedbackText).toBe('Great support and quick resolution!');
    expect(feedback.date).toEqual(new Date('2023-12-01'));
    expect(feedback.userId).toBe(101);
    expect(feedback.agentId).toBe(202);
    expect(feedback.ticketId).toBe(303);
    expect(feedback.category).toBe('Support');
    expect(feedback.rating).toBe(5);
  });

});
