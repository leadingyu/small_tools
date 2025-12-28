#!/usr/bin/env python3
"""
open_ai.py

Analyze a local PNG (or any image) using the OpenAI Responses API.

Setup:
  pip install --upgrade openai
  export OPENAI_API_KEY="your_key"

Usage:
  python open_ai.py /path/to/image.png
  python open_ai.py /path/to/image.png --prompt "Describe this image in detail"
  python open_ai.py /path/to/image.png --model "gpt-4.1-mini" --detail low
"""

import argparse
import base64
import mimetypes
import os
import sys
from pathlib import Path

from openai import OpenAI


def file_to_data_url(path: Path) -> str:
    if not path.exists() or not path.is_file():
        raise FileNotFoundError(f"File not found: {path}")

    mime, _ = mimetypes.guess_type(str(path))
    if mime is None:
        # Fallback if unknown
        mime = "application/octet-stream"

    data = path.read_bytes()
    b64 = base64.b64encode(data).decode("utf-8")
    return f"data:{mime};base64,{b64}"


def main() -> int:
    parser = argparse.ArgumentParser(description="Analyze an image with the OpenAI API (Responses API).")
    parser.add_argument("image_path", help="Path to a local image file (e.g., .png, .jpg)")
    parser.add_argument(
        "--prompt",
        default="Describe the contents of this image in detail.",
        help="Instruction for what analysis you want.",
    )
    parser.add_argument(
        "--model",
        default="gpt-4.1-mini",
        help="Model name (must support vision).",
    )
    parser.add_argument(
        "--detail",
        choices=["low", "high", "auto"],
        default="auto",
        help="Image detail level (if supported by the model).",
    )
    parser.add_argument(
        "--max-output-tokens",
        type=int,
        default=800,
        help="Limit for the text output.",
    )
    args = parser.parse_args()

    if not os.getenv("OPENAI_API_KEY"):
        print("ERROR: OPENAI_API_KEY env var is not set.", file=sys.stderr)
        return 2

    image_path = Path(args.image_path).expanduser().resolve()
    data_url = file_to_data_url(image_path)

    client = OpenAI()

    # Responses API: provide text + image as multimodal input.
    # See OpenAI "Images and vision" and "Migrate to Responses" guides. :contentReference[oaicite:1]{index=1}
    resp = client.responses.create(
        model=args.model,
        input=[
            {
                "role": "user",
                "content": [
                    {"type": "input_text", "text": args.prompt},
                    {"type": "input_image", "image_url": data_url, "detail": args.detail},
                ],
            }
        ],
        max_output_tokens=args.max_output_tokens,
    )

    # The SDK provides a convenience accessor for the generated text.
    print(resp.output_text.strip())
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
