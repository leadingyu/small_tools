from notion_client import Client

# 1️⃣ Initialize Notion client
# NOTE: Your token MUST start with "secret_" - get it from https://www.notion.so/my-integrations
# Replace YOUR_TOKEN_HERE with your actual token from Notion integration settings
notion = Client(auth="ntn_xxxxx")

page_ids = [
    "29b2e9f33f0380f891d7f9206418ad55",
]

# 3️⃣ Content to append
message = "Starting from small. With beginner's mindsets there are tons of possibilities."

def normalize_id(page_id: str) -> str:
    page_id = page_id.replace("-", "")
    return f"{page_id[0:8]}-{page_id[8:12]}-{page_id[12:16]}-{page_id[16:20]}-{page_id[20:]}"

# 4️⃣ Append block to the end of each page
for pid in page_ids:
    try:
        normalized_pid = normalize_id(pid)
        notion.blocks.children.append(
            block_id=normalized_pid,
            children=[
                {
                    "object": "block",
                    "type": "paragraph",
                    "paragraph": {
                        "rich_text": [
                            {
                                "type": "text",
                                "text": {"content": message}
                            }
                        ]
                    },
                }
            ]
        )
        print(f"✅ Appended to page {pid}")
    except Exception as e:
        print(f"❌ Error appending to page {pid}: {e}")
        print(f"Error type: {type(e).__name__}")
