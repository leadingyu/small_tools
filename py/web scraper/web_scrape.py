import csv
import re
from urllib.parse import urljoin

import requests
from bs4 import BeautifulSoup

BASE_URL = "https://openaccess.thecvf.com"
LIST_URL = "https://openaccess.thecvf.com/CVPR2024?day=all"
OUTPUT_CSV = "cvpr2024_papers.csv"


def fetch_html(url: str) -> str:
    """Fetch HTML content for a given URL."""
    headers = {
        "User-Agent": (
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
            "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        )
    }
    resp = requests.get(url, headers=headers, timeout=30)
    resp.raise_for_status()
    return resp.text


def parse_bibtex_block(bib_text: str):
    """
    Parse BibTeX text from a div.bibref.pre-white-space block and
    extract title and authors (raw string).
    """
    # Remove excessive whitespace to make regex a bit more robust
    text = " ".join(bib_text.split())

    # Extract title
    title_match = re.search(r"title\s*=\s*{([^}]*)}", text, re.IGNORECASE)
    title = title_match.group(1).strip() if title_match else ""

    # Extract authors
    author_match = re.search(r"author\s*=\s*{([^}]*)}", text, re.IGNORECASE)
    raw_authors = author_match.group(1).strip() if author_match else ""

    # BibTeX authors are usually "Last, First and Last2, First2 and ..."
    # We'll split by ' and ' and re-join with '; ' inside braces.
    authors_list = [a.strip() for a in raw_authors.split(" and ") if a.strip()]
    authors_formatted = "{" + "; ".join(authors_list) + "}" if authors_list else "{}"

    return title, authors_formatted


def find_pdf_link(bib_div):
    """
    Given a bibref div, try to find the corresponding PDF link.
    Strategy:
        1) Look in the same parent container for an <a> whose text contains 'pdf'
           or whose href ends with '.pdf'
        2) If not found, walk backwards in the DOM for an <a> ending with '.pdf'
    """
    # Try searching within the closest "paper container" (parent div)
    container = bib_div.find_parent("div")
    pdf_link = None

    # Search anchors in the container first
    if container:
        for a in container.find_all("a", href=True):
            href = a["href"]
            txt = (a.get_text() or "").strip().lower()
            if href.lower().endswith(".pdf") or "pdf" == txt:
                pdf_link = href
                break

    # Fallback: look at previous anchors if needed
    if not pdf_link:
        prev_a = bib_div.find_previous("a", href=True)
        while prev_a:
            href = prev_a["href"]
            if href.lower().endswith(".pdf"):
                pdf_link = href
                break
            prev_a = prev_a.find_previous("a", href=True)

    if pdf_link:
        return urljoin(BASE_URL, pdf_link)
    return ""


def scrape_cvpr2024():
    html = fetch_html(LIST_URL)
    soup = BeautifulSoup(html, "html.parser")

    rows = []

    # Each bibtex block is in a div with class "bibref pre-white-space"
    for bib_div in soup.select("div.bibref.pre-white-space"):
        bib_text = bib_div.get_text("\n", strip=True)
        title, authors = parse_bibtex_block(bib_text)
        pdf_url = find_pdf_link(bib_div)

        if not title:
            # Skip if we couldn't parse a title (likely not a valid paper entry)
            continue

        rows.append({
            "title": title,
            "authors": authors,
            "pdf": pdf_url
        })

    # Write to CSV
    with open(OUTPUT_CSV, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=["title", "authors", "pdf"])
        writer.writeheader()
        for row in rows:
            writer.writerow(row)

    print(f"Extracted {len(rows)} papers to {OUTPUT_CSV}")


if __name__ == "__main__":
    scrape_cvpr2024()
