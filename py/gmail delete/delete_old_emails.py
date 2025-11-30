from __future__ import print_function

import os.path
from datetime import date, timedelta

from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build

# ----------------------------------------------------
# SETTINGS
# ----------------------------------------------------
# Scope:
# - gmail.modify is enough to move to Trash or delete messages
SCOPES = ["https://www.googleapis.com/auth/gmail.modify"]

# Safety: set to True to only print which messages would be deleted
DRY_RUN = False

# How many days ~ 5 years (approx)
DAYS_3_YEARS = 5 * 365


def get_gmail_service():
    """Authorize and return a Gmail API service instance."""
    creds = None
    # token.json stores the user's access and refresh tokens,
    # and is created automatically when the auth flow completes the first time.
    if os.path.exists("token.json"):
        creds = Credentials.from_authorized_user_file("token.json", SCOPES)

    # If there are no (valid) credentials, let user log in.
    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            if not os.path.exists("credentials.json"):
                raise FileNotFoundError(
                    "credentials.json not found. Please download OAuth 2.0 client credentials "
                    "from Google Cloud Console (Application type: Desktop app) and save as credentials.json"
                )
            
            try:
                flow = InstalledAppFlow.from_client_secrets_file(
                    "credentials.json", SCOPES
                )
                creds = flow.run_local_server(port=0)
            except ValueError as e:
                if "Client secrets must be for a web or installed app" in str(e):
                    raise ValueError(
                        "credentials.json has incorrect format. It must be an OAuth 2.0 client credentials file "
                        "for a Desktop/Installed app (not a service account).\n"
                        "Please download the correct credentials from Google Cloud Console:\n"
                        "1. Go to APIs & Services > Credentials\n"
                        "2. Create OAuth 2.0 Client ID (Application type: Desktop app)\n"
                        "3. Download and save as credentials.json"
                    ) from e
                raise
        # Save credentials for later runs
        with open("token.json", "w") as token:
            token.write(creds.to_json())

    service = build("gmail", "v1", credentials=creds)
    return service


def three_years_ago_query():
    """Return a Gmail search query string for messages older than 3 years."""
    cutoff_date = date.today() - timedelta(days=DAYS_3_YEARS)
    # Gmail uses YYYY/MM/DD
    cutoff_str = cutoff_date.strftime("%Y/%m/%d")
    # 'before:' finds messages strictly before this date
    return f"before:{cutoff_str}"


def list_message_ids(service, user_id="me", query="", batch_size=500):
    """
    Generator that yields message IDs matching the query.
    """
    page_token = None

    while True:
        response = (
            service.users()
            .messages()
            .list(
                userId=user_id,
                q=query,
                pageToken=page_token,
                maxResults=batch_size,
            )
            .execute()
        )

        messages = response.get("messages", [])
        for m in messages:
            yield m["id"]

        page_token = response.get("nextPageToken")
        if not page_token:
            break


def batch_trash_messages(service, message_ids, user_id="me", batch_size=1000):
    """
    Move messages to Trash in batches.
    """
    msg_ids = list(message_ids)
    total = len(msg_ids)
    print(f"Found {total} messages to process.")

    if total == 0:
        return

    if DRY_RUN:
        print("DRY_RUN is True. No messages will actually be moved to Trash.")
        # For safety, print first few IDs only:
        print("Example message IDs:")
        for mid in msg_ids[:10]:
            print("  ", mid)
        return

    # Process IDs in chunks of batch_size
    for i in range(0, total, batch_size):
        chunk = msg_ids[i : i + batch_size]
        print(f"Trashing messages {i+1}–{min(i+len(chunk), total)} of {total}...")

        body = {"ids": chunk}
        # You can also use users().messages().batchDelete for permanent delete
        service.users().messages().batchModify(
            userId=user_id,
            body={"ids": chunk, "removeLabelIds": [], "addLabelIds": ["TRASH"]},
        ).execute()

    print("Done. All selected messages were moved to Trash.")


def main():
    service = get_gmail_service()

    query = three_years_ago_query()
    print(f"Using Gmail search query: {query}")

    ids = list_message_ids(service, user_id="me", query=query)
    batch_trash_messages(service, ids, user_id="me")


if __name__ == "__main__":
    main()
