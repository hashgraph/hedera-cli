import { Filter } from '../../types/api/shared';

/**
 * Constructs the query part of the URL based on provided filters.
 * @param baseUrl The base URL without query parameters.
 * @param filters Array of filters to apply.
 * @returns The full URL with query parameters.
 */
function constructQueryUrl(baseUrl: string, filters: Filter[]): string {
  if (filters.length === 0) {
    return baseUrl;
  }

  const queryParams = filters.map(
    (filter) =>
      `${encodeURIComponent(filter.field)}=${
        filter.operation
      }:${encodeURIComponent(filter.value)}`,
  );
  return `${baseUrl}?${queryParams.join('&')}&limit=100`;
}

const apiUtils = {
  constructQueryUrl,
};

export default apiUtils;
