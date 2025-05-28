import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
@Injectable({
  providedIn: 'root'
})
export class FeedbackService {
  constructor(private http: HttpClient) { }
  getAllFeedbacks(){
    return this.http.get('/api/feedbacks')
  }

  // constructor() { }
}
