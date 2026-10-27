import { access } from 'node:fs/promises';
import path from 'node:path';
import { readSheet } from 'read-excel-file/node';

export interface LoginCredentials {
    username: string;
    password: string;
}

export async function readLoginData(
    filePath = path.resolve(process.cwd(), 'test-data', 'login.xlsx'),
    sheetName = 'Login',
): Promise<LoginCredentials[]> {
    try {
        await access(filePath);
    } catch (error) {
        if (
            error !== null &&
            typeof error === 'object' &&
            'code' in error &&
            error.code === 'ENOENT'
        ) {
            throw new Error(`Login workbook was not found: ${filePath}`);
        }
        throw error;
    }

    const [headers, ...rows] = await readSheet(filePath, sheetName);
    if (!headers) {
        throw new Error(`Worksheet "${sheetName}" is empty`);
    }

    const usernameColumn = headers.findIndex(
        (value) => typeof value === 'string' && value.trim().toLowerCase() === 'username',
    );
    const passwordColumn = headers.findIndex(
        (value) => typeof value === 'string' && value.trim().toLowerCase() === 'password',
    );

    if (usernameColumn < 0 || passwordColumn < 0) {
        throw new Error(
            `Worksheet "${sheetName}" must contain "username" and "password" headers in row 1`,
        );
    }

    const credentials: LoginCredentials[] = [];
    rows.forEach((row, index) => {
        if (row.every((value) => value === null)) {
            return;
        }

        const username = row[usernameColumn];
        const password = row[passwordColumn];
        if (typeof username !== 'string' || typeof password !== 'string') {
            throw new Error(
                `Row ${index + 2} in worksheet "${sheetName}" must have text values for username and password`,
            );
        }

        const normalizedUsername = username.trim();
        if (!normalizedUsername || !password.trim()) {
            throw new Error(
                `Row ${index + 2} in worksheet "${sheetName}" has an empty username or password`,
            );
        }

        credentials.push({ username: normalizedUsername, password });
    });

    if (credentials.length === 0) {
        throw new Error(`No login data found in worksheet "${sheetName}"`);
    }

    return credentials;
}
