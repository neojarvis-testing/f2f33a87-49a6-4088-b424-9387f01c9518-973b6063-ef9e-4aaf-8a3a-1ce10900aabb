import { TestBed } from '@angular/core/testing';

import { TicketService } from './ticket.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('TicketService', () => {
  let service: TicketService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(TicketService);
  });

  fit('Frontend_should_ticket_service', () => {
    expect(service).toBeTruthy();
  });
});
