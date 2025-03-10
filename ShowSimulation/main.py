import pandas as pd

# Încarcă datele din fișierul CSV generat de Java
file_path = "simulation_results.csv"
df = pd.read_csv(file_path)

# Afișează primele rânduri pentru verificare
print(df.head())
