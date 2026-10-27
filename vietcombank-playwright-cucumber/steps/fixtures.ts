import { test as base } from 'playwright-bdd';

/**
 * Base fixtures file for playwright-bdd.
 * Extend this file to add custom fixtures shared across all step definitions.
 * The `importTestFrom` field in playwright.config.ts points here so that
 * generated spec files import the correct `test` instance.
 */
export const test = base;
export const expect = base.expect;
