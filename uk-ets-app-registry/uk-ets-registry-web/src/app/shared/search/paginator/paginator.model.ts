export interface Pagination {
  currentPage: number;
  totalResults: number;
  pageSize: number;
}

export interface PageParameters {
  page: number; // Starts from zero, e.g 4th page is 3
  pageSize: number; // The page size that user selected
}
