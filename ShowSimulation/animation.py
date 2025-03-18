import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.patches as patches
import matplotlib.animation as animation
import numpy as np
import random

import matplotlib

matplotlib.use('TkAgg')

# Citire date din CSV
file_path = "../cloudsim-7.0/simulation_results.csv"
data = pd.read_csv(file_path)

# Obtine lista unica de Host-uri si VM-uri
hosts = sorted(data['Host'].unique())
vms = sorted(data['VM'].unique())

max_time = data["Finish Time"].max()
max_energy = data["Energy Consumption"].max()

# Setare dimensiuni matrice pentru host-uri (ex: 3x3 grid)
grid_size = int(np.ceil(np.sqrt(len(hosts))))  # Creeaza o matrice patrata
host_grid_positions = {}

# Initializare figura pentru alocarea VM-urilor
fig, ax = plt.subplots(figsize=(12, 10))
ax.set_xlim(-1, grid_size * 10)
ax.set_ylim(-1, grid_size * 10)
ax.set_xticks([])
ax.set_yticks([])
ax.set_title(f"Final Execution Time: {max_time}", fontsize=14, fontweight='bold')

# Initializare figura pentru consumul de energie
fig_energy, ax_energy = plt.subplots(figsize=(10, 6))
ax_energy.set_xlim(0, max_time + 5)
ax_energy.set_ylim(0, max_energy + 1)
ax_energy.set_xlabel("Time")
ax_energy.set_ylabel("Energy Consumption (kWh)")
ax_energy.set_title(f"Max Energy Consumption: {max_energy:.2f} kWh", fontsize=14, fontweight='bold')

# Dictionar pentru stocarea pozitiilor VM-urilor si Cloudlet-urilor
vm_positions = {}

# Deseneaza host-urile în format de grila
for idx, host in enumerate(hosts):
    row, col = divmod(idx, grid_size)
    host_x, host_y = col * 10, row * 10
    host_grid_positions[host] = (host_x + 5, host_y + 5)  # Centrat în celula

    # Deseneaza host-ul ca un patrat mare
    host_rect = patches.Rectangle((host_x, host_y), 8, 8, color='gray', alpha=0.5, edgecolor='black', linewidth=2)
    ax.add_patch(host_rect)

    # Eticheta pentru host
    ax.text(host_x + 4, host_y + 8.5, f"Host {host}", fontsize=12, ha='center', color='black', fontweight='bold')


# Functia de animatie pentru VM-uri si Cloudlet-uri
# Funcția de animație pentru VM-uri și Cloudlet-uri (cu timp în titlu)
def update(frame):
    ax.clear()
    ax.set_xlim(-1, grid_size * 10)
    ax.set_ylim(-1, grid_size * 10)
    ax.set_xticks([])
    ax.set_yticks([])
    ax.set_title(f"Final Execution Time: {max_time} | Current Time: {frame}", fontsize=14, fontweight='bold')

    # Re-desenare host-uri în matrice
    for host, (host_x, host_y) in host_grid_positions.items():
        host_rect = patches.Rectangle((host_x - 4, host_y - 4), 8, 8, color='gray', alpha=0.5, edgecolor='black',
                                      linewidth=2)
        ax.add_patch(host_rect)
        ax.text(host_x, host_y + 4.5, f"Host {host}", fontsize=12, ha='center', color='black', fontweight='bold')

    # Selectează cloudlet-urile active
    active_tasks = data[(data['Start Time'] <= frame) & (data['Finish Time'] >= frame)]

    for _, row in active_tasks.iterrows():
        host = row['Host']
        vm = row['VM']
        cloudlet_id = row['CloudletID']

        # Poziția VM-ului în Host
        if vm not in vm_positions:
            vm_positions[vm] = (
                host_grid_positions[host][0] + random.uniform(-3, 3),
                host_grid_positions[host][1] + random.uniform(-3, 3)
            )

        # Poziția Cloudlet-ului în VM
        cloudlet_x, cloudlet_y = vm_positions[vm][0] + random.uniform(-1, 1), vm_positions[vm][1] + random.uniform(-1,
                                                                                                                   1)

        # Desenează Cloudlet-ul ca un cerc albastru
        ax.scatter(cloudlet_x, cloudlet_y, s=600, c='blue', alpha=0.8, edgecolors='black')

        # Etichetă pentru Cloudlet
        ax.text(cloudlet_x, cloudlet_y + 0.5, f"C{cloudlet_id}", fontsize=12, ha='center', color='white',
                fontweight='bold')

        # Desenează VM-ul ca un triunghi portocaliu
        triangle = [[cloudlet_x, cloudlet_y + 1.2],  # Vârf sus
                    [cloudlet_x - 1.2, cloudlet_y - 1.2],  # Stânga jos
                    [cloudlet_x + 1.2, cloudlet_y - 1.2]]  # Dreapta jos

        vm_triangle = patches.Polygon(triangle, closed=True, color='orange', alpha=0.7, edgecolor='black')
        ax.add_patch(vm_triangle)

        # Etichetă pentru VM
        ax.text(cloudlet_x, cloudlet_y - 1.5, f"VM {vm}", fontsize=12, ha='center', color='black', fontweight='bold')


# Functia de animatie pentru consumul de energie
def update_energy(frame):
    ax_energy.clear()
    ax_energy.set_xlim(0, max_time + 5)
    ax_energy.set_ylim(0, max_energy + 1)
    ax_energy.set_xlabel("Time")
    ax_energy.set_ylabel("Energy Consumption (kWh)")
    ax_energy.set_title(f"Max Energy Consumption: {max_energy:.2f} kWh", fontsize=14, fontweight='bold')

    active_tasks = data[(data['Start Time'] <= frame) & (data['Finish Time'] >= frame)]

    for _, row in active_tasks.iterrows():
        ax_energy.scatter(row["Finish Time"], row["Energy Consumption"], s=200, c='red', alpha=0.7, edgecolors='black')
        ax_energy.text(row["Finish Time"], row["Energy Consumption"] + 0.1, f"VM {row['VM']}", fontsize=10, ha='center',
                       color='black', fontweight='bold')


# Creeaza animatiile
ani_vm = animation.FuncAnimation(fig, update, frames=range(int(data['Finish Time'].max()) + 5), interval=500)
ani_energy = animation.FuncAnimation(fig_energy, update_energy, frames=range(int(data['Finish Time'].max()) + 5),
                                     interval=500)

plt.show()
