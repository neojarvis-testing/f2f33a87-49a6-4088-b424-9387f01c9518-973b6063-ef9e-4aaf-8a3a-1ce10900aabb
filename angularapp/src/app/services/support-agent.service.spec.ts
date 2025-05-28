import { TestBed } from '@angular/core/testing';

import { SupportAgentService } from './support-agent.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('SupportAgentService', () => {
  let service: SupportAgentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(SupportAgentService);
  });

  fit('Frontend_should_support_agent_service', () => {
    expect(service).toBeTruthy();
  });
});
