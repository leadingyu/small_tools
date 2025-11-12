# Google Sheets Authentication Setup Guide

This guide will help you set up authentication to write data to Google Sheets.

## Step 1: Create a Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Click on the project dropdown at the top
3. Click "New Project"
4. Enter a project name (e.g., "Sheets API Project")
5. Click "Create"

## Step 2: Enable Google Sheets API and Google Drive API

1. In your Google Cloud project, go to "APIs & Services" > "Library"
2. Search for "Google Sheets API" and click on it
3. Click "Enable"
4. Go back to the Library
5. Search for "Google Drive API" and click on it
6. Click "Enable"

## Step 3: Create a Service Account

1. Go to "APIs & Services" > "Credentials"
2. Click "Create Credentials" > "Service Account"
3. Enter a name for the service account (e.g., "sheets-writer")
4. Click "Create and Continue"
5. Skip the optional steps and click "Done"

## Step 4: Create and Download Service Account Key

1. In the "Credentials" page, find your service account under "Service Accounts"
2. Click on the service account email
3. Go to the "Keys" tab
4. Click "Add Key" > "Create new key"
5. Select "JSON" format
6. Click "Create"
7. The JSON file will be downloaded automatically

## Step 5: Rename and Place the Credentials File

1. Rename the downloaded JSON file to `credentials.json`
2. Move it to the same directory as `process_data.py` (the project root: `/Users/leadingyu88/Documents/cursor_code/python/`)

## Step 6: Share Google Sheet with Service Account

1. Open your Google Sheet: https://docs.google.com/spreadsheets/d/1p8YsxTOucLesIsn4PKDtoeRH1eojaEnCsKPLc3Ps0ow/edit
2. Click the "Share" button (top right)
3. Copy the service account email from the `credentials.json` file (look for `"client_email"` field)
4. Paste the service account email in the "Add people and groups" field
5. Make sure to give it "Editor" permissions
6. Uncheck "Notify people" (optional, since it's a service account)
7. Click "Share"

## Step 7: Install Dependencies

Run the following command to install the required packages:

```bash
pip install -r requirements.txt
```

Or install manually:

```bash
pip install gspread google-auth pandas
```

## Step 8: Run the Script

```bash
python3 process_data.py
```

## Troubleshooting

- **Permission denied error**: Make sure you've shared the Google Sheet with the service account email
- **File not found error**: Ensure `credentials.json` is in the same directory as `process_data.py`
- **API not enabled**: Verify that both Google Sheets API and Google Drive API are enabled in your Google Cloud project

## Security Note

- Never commit `credentials.json` to version control (Git)
- Keep your credentials file secure and private
- The service account has access to any sheets you share with it

