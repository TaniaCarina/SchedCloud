import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.animation as animation

import matplotlib
matplotlib.use('TkAgg')  # Setează un backend compatibil


# Citește datele din fișier
file_path = "simulation_results.csv"  # Înlocuiește cu calea corectă a fișierului tău CSV
data = pd.read_csv(file_path)

# Inițializează figura
fig, ax = plt.subplots()
ax.set_xlim(0, data["Finish Time"].max() + 5)
ax.set_ylim(0, data["VM"].max() + 2)
ax.set_xlabel("Time")
ax.set_ylabel("VM ID")
ax.set_title("Cloudlet Execution Timeline")

# Liste pentru animație
scat = ax.scatter([], [], c='blue', s=100, label="Cloudlets")
texts = []


# Funcția de inițializare
def init():
    scat.set_offsets([[0,0]])
    return scat,


# Funcția de animație
def update(frame):
    ax.clear()
    ax.set_xlim(0, data["Finish Time"].max() + 5)
    ax.set_ylim(0, data["VM"].max() + 2)
    ax.set_xlabel("Time")
    ax.set_ylabel("VM ID")
    ax.set_title("Cloudlet Execution Timeline")

    # Selectează doar cloudlets care au pornit până la acest frame
    current_data = data[data["Start Time"] <= frame]

    if not current_data.empty:
        scatter_data = list(zip(current_data["Start Time"], current_data["VM"]))
        ax.scatter(*zip(*scatter_data), c='blue', s=100)

        # Adaugă text (VM ID) deasupra fiecărei buline
        for _, row in current_data.iterrows():
            ax.text(row["Start Time"], row["VM"], f"VM {int(row['VM'])}", fontsize=10, ha='left', va='bottom')

    return scat,


# Creează animația
ani = animation.FuncAnimation(fig, update, frames=range(0, int(data["Finish Time"].max()) + 5), init_func=init,
                              interval=500)

plt.legend()
plt.show()
