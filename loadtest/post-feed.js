import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const SIZE = Number(__ENV.SIZE || 10);

export const options = {
  summaryTrendStats: ['avg', 'min', 'med', 'p(95)', 'p(99)', 'max'],
  thresholds: {
    http_req_failed: ['rate<0.01'],
  },
  scenarios: {
    ramp: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '20s', target: 50 },
        { duration: '30s', target: 50 },
        { duration: '20s', target: 200 },
        { duration: '30s', target: 200 },
        { duration: '20s', target: 500 },
        { duration: '30s', target: 500 },
        { duration: '20s', target: 1000 },
        { duration: '30s', target: 1000 },
        { duration: '10s', target: 0 },
      ],
    },
  },
};

export default function () {
  const res = http.get(`${BASE_URL}/api/v1/posts?size=${SIZE}`);
  check(res, { 'status is 200': (r) => r.status === 200 });
}
