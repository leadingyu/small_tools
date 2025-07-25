import pandas as pd
import sys

# Get input file from command line argument
if len(sys.argv) > 1:
    input_file = sys.argv[1]
else:
    print("Please provide a CSV file name as argument")
    sys.exit(1)

# Always load as string to preserve formatting
df = pd.read_csv(input_file, dtype=str)

# trim the prefix and only keep the part after '='
def trim_optblue_network_id(val):
    if pd.isna(val) or val == '':
        return val
    return val.split('=')[-1] if '=' in val else val

# Apply only to the optblue_network_id column
df['optblue_network_id'] = df['optblue_network_id'].apply(trim_optblue_network_id)

# Create output filename by inserting '_trimmed' before '.csv'
output_file = input_file.replace('.csv', '_trimmed.csv')

# Save to a new CSV file
df.to_csv(output_file, index=False)

print("Trimming complete. Output saved to:", output_file)