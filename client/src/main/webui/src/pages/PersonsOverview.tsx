import { useEffect, useState } from "react";
import {
    Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { Plus, Trash2, Pencil } from "lucide-react";
import { toast } from "sonner"; // Optional: Für besseres Feedback

type Person = {
    id: number;
    name: string;
    password: string;
    email?: string;
    accessToken?: string;
    accessUrl?: string;
};

interface PersonsOverviewProps {
    token: string;
    onLogout: () => void;
}

export function PersonsOverview({ token, onLogout }: PersonsOverviewProps) {
    const [persons, setPersons] = useState<Person[]>([]);
    const [selected, setSelected] = useState<Person | null>(null);
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [formData, setFormData] = useState({ name: "", email: "", password: "" });

    const fetchPersons = async () => {
        try {
            const response = await fetch("http://localhost:8081/api/persons", {
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`
                },
            });

            if (!response.ok) {
                throw new Error(`HTTP-Fehler! Status: ${response.status}`);
            }

            const data = await response.json();
            setPersons(data);
        } catch (error) {
            console.error("Fehler beim Laden der Personen:", error);
            toast.error("Personen konnten nicht geladen werden.");
        }
    };

    useEffect(() => {
        if (token) {
            fetchPersons();
        }
    }, [token]);

    const handleOpenDialog = (person?: Person) => {
        if (person) {
            // Beim Bearbeiten das Passwortfeld leer lassen, da man es nicht auslesen kann
            setFormData({ name: person.name, email: person.email ?? "", password: "" });
            setSelected(person);
        } else {
            setFormData({ name: "", email: "", password: "" });
            setSelected(null);
        }
        setIsDialogOpen(true);
    };

    const handleSave = async () => {
        const method = selected ? "PUT" : "POST";
        const url = selected
            ? `http://localhost:8081/api/persons/${selected.id}`
            : "http://localhost:8081/api/persons"; // Vollständige URL auch hier verwenden

        try {
            const response = await fetch(url, {
                method,
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(formData),
            });

            if (!response.ok) {
                throw new Error("Speichern fehlgeschlagen");
            }

            toast.success(selected ? "Person erfolgreich geändert." : "Person erfolgreich angelegt.");
            setIsDialogOpen(false);
            fetchPersons(); // << NEU: Daten nach dem Speichern neu laden
        } catch (error) {
            toast.error("Ein Fehler ist aufgetreten.");
            console.error("Fehler beim Speichern:", error);
        }
    };

    const handleDelete = async (id: number) => {
        if (!window.confirm("Soll die Person wirklich gelöscht werden?")) return;

        try {
            const response = await fetch(`http://localhost:8081/api/persons/${id}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` },
            });

            if (!response.ok) {
                throw new Error("Löschen fehlgeschlagen");
            }

            toast.success("Person gelöscht.");
            fetchPersons(); // << NEU: Daten nach dem Löschen neu laden
        } catch (error) {
            toast.error("Löschen fehlgeschlagen.");
            console.error("Fehler beim Löschen:", error);
        }
    };

    return (
        <div className="p-4">
            <div className="flex justify-between items-center mb-4">
                <h1 className="text-2xl font-bold">Personenverwaltung</h1>
                <div>
                    <Button onClick={() => handleOpenDialog()} className="mr-4"><Plus className="mr-2 h-4 w-4" /> Neue Person</Button>
                    <Button onClick={onLogout} variant="outline">Logout</Button>
                </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
                {persons.map((person) => (
                    <Card key={person.id}>
                        <CardContent className="p-4 flex flex-col justify-between h-full">
                            <div>
                                <div className="text-lg font-semibold">{person.name}</div>
                                {person.email && <div className="text-sm text-muted-foreground">{person.email}</div>}
                            </div>
                            <div className="mt-4 flex justify-end gap-2">
                                <Button variant="ghost" size="icon" onClick={() => handleOpenDialog(person)}>
                                    <Pencil className="w-4 h-4" />
                                </Button>
                                <Button variant="ghost" size="icon" onClick={() => handleDelete(person.id)}>
                                    <Trash2 className="w-4 h-4 text-red-500" />
                                </Button>
                            </div>
                        </CardContent>
                    </Card>
                ))}
            </div>

            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>{selected ? "Person bearbeiten" : "Neue Person anlegen"}</DialogTitle>
                    </DialogHeader>
                    <div className="space-y-4 py-2">
                        <Input
                            placeholder="Name"
                            value={formData.name}
                            onChange={(e) => setFormData((d) => ({ ...d, name: e.target.value }))}
                        />
                        <Input
                            placeholder="E-Mail"
                            type="email"
                            value={formData.email}
                            onChange={(e) => setFormData((d) => ({ ...d, email: e.target.value }))}
                        />
                        <Input
                            // highlight-start
                            type="password"
                            // highlight-end
                            placeholder={selected ? "Neues Passwort (optional)" : "Passwort"}
                            value={formData.password}
                            onChange={(e) => setFormData((d) => ({ ...d, password: e.target.value }))}
                        />
                    </div>
                    <DialogFooter>
                        <Button onClick={handleSave}>
                            {selected ? "Änderungen speichern" : "Person anlegen"}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    );
}