export interface PasswordStrengthRequest {
  readonly password: string;
}

// Integer from 0-4 (useful for implementing a strength bar)
// 0 Weak        （guesses < ^ 3 10）
// 1 Fair        （guesses <^ 6 10）
// 2 Good        （guesses <^ 8 10）
// 3 Strong      （guesses < 10 ^ 10）
// 4 Very strong （guesses >= 10 ^ 10）
export type Score = 0 | 1 | 2 | 3 | 4;

export interface PasswordStrengthResponse {
  readonly score: Score;
}
