import sqlite3
import pandas as pd
import gspread
from google.oauth2.service_account import Credentials

# Configuration
SPREADSHEET_ID = '1p8YsxTOucLesIsn4PKDtoeRH1eojaEnCsKPLc3Ps0ow'
SHEET_NAME = 'Data'
SCOPES = [
    'https://www.googleapis.com/auth/spreadsheets',
    'https://www.googleapis.com/auth/drive'
]

# Step 1: Connect to SQLite database
db_path = 'events.db'
conn = sqlite3.connect(db_path)

# Step 2: Read SQL query from data.sql
with open('data.sql', 'r', encoding='utf-8') as f:
    sql_query = f.read()

# Step 3: Execute SQL query and get results
print("Executing SQL query...")
df = pd.read_sql_query(sql_query, conn)
print(f"Retrieved {len(df)} rows")

# Step 4: Close database connection
conn.close()

# Step 5: Authenticate with Google Sheets
print("Authenticating with Google Sheets...")
creds = Credentials.from_service_account_file('credentials.json', scopes=SCOPES)
client = gspread.authorize(creds)

# Step 6: Open the spreadsheet
print(f"Opening spreadsheet: {SPREADSHEET_ID}")
spreadsheet = client.open_by_key(SPREADSHEET_ID)

# Step 7: Get or create the 'Data' sheet
try:
    worksheet = spreadsheet.worksheet(SHEET_NAME)
    print(f"Found existing '{SHEET_NAME}' sheet")
except gspread.exceptions.WorksheetNotFound:
    worksheet = spreadsheet.add_worksheet(title=SHEET_NAME, rows=1000, cols=20)
    print(f"Created new '{SHEET_NAME}' sheet")

# Step 8: Clear existing content in the 'Data' sheet
print(f"Clearing existing content in '{SHEET_NAME}' sheet...")
worksheet.clear()

# Step 9: Write headers
print("Writing headers...")
headers = df.columns.tolist()
worksheet.append_row(headers)

# Step 10: Write data rows
print(f"Writing {len(df)} rows of data...")
# Convert DataFrame to list of lists
values = df.values.tolist()
if values:
    worksheet.append_rows(values)

print(f"Data successfully written to Google Sheet '{SHEET_NAME}' sheet")
print(f"Total rows written: {len(df)}")
print(f"Columns: {', '.join(headers)}")
print(f"Spreadsheet URL: https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}")

