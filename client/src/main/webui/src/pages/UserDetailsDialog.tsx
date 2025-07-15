// client/src/main/webui/src/components/UserDetailsDialog.tsx
import { useState, useEffect } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

type UserDetails = {
    groesse: number;
    gewicht: number;
    geschlecht: string;
    bewegungsProfil: string;
    dateOfBirth: string;
    kcalVerbrauch: number;
};

type Props = {
    open: boolean;
    onClose: () => void;
    person: string;
    token: string;
};

export function UserDetailsDialog({ open, onClose, person, token }: Props) {
    const [details, setDetails] = useState<UserDetails>({
        kcalVerbrauch: 0,
        bewegungsProfil: "WENIG",
        geschlecht: "m",
        dateOfBirth: "",
        groesse: 0,
        gewicht: 0
    });

    useEffect(() => {
        if (open) {
            fetch(`http://localhost:8081/api/user-details/${person}`, {
                headers: { Authorization: `Bearer ${token}` }
            })
                .then(r => r.ok ? r.json() : null)
                .then(d => d && setDetails(d));
        }
    }, [open, person, token]);

    const handleSave = async () => {
        await fetch(`http://localhost:8081/api/user-details/${person}`, {
            method: "POST",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
            body: JSON.stringify(details)
        });
        onClose();
    };

    return (
        <Dialog open={open} onOpenChange={onClose}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Persönliche Daten</DialogTitle>
                </DialogHeader>

                <div className="space-y-4 py-2">
                    <div className="flex flex-col space-y-1">
                        <Label htmlFor="groesse">Größe (cm)</Label>
                        <Input
                            id="groesse"
                            type="number"
                            value={details.groesse}
                            onChange={(e) => setDetails((d) => ({ ...d, groesse: +e.target.value }))}
                        />
                    </div>

                    <div className="flex flex-col space-y-1">
                        <Label htmlFor="gewicht">Gewicht (kg)</Label>
                        <Input
                            id="gewicht"
                            type="number"
                            value={details.gewicht}
                            onChange={(e) => setDetails((d) => ({ ...d, gewicht: +e.target.value }))}
                        />
                    </div>

                    <div className="flex flex-col space-y-1">
                        <Label htmlFor="geburtsdatum">Geburtsdatum</Label>
                        <Input
                            id="geburtsdatum"
                            type="date"
                            value={details.dateOfBirth}
                            onChange={(e) => setDetails((d) => ({ ...d, dateOfBirth: e.target.value }))}
                        />
                    </div>

                    <div className="flex flex-col space-y-1">
                        <Label htmlFor="geschlecht">Geschlecht</Label>
                        <select
                            id="geschlecht"
                            className="border rounded p-2 dark:bg-black"
                            value={details.geschlecht}
                            onChange={(e) => setDetails((d) => ({ ...d, geschlecht: e.target.value }))}
                        >
                            <option value="m">Männlich</option>
                            <option value="w">Weiblich</option>
                        </select>
                    </div>

                    <div className="flex flex-col space-y-1">
                        <Label htmlFor="bewegung">Bewegungsprofil</Label>
                        <select
                            id="bewegung"
                            className="border rounded p-2 dark:bg-black"
                            value={details.bewegungsProfil}
                            onChange={(e) =>
                                setDetails((d) => ({ ...d, bewegungsProfil: e.target.value }))
                            }
                        >
                            <option value="VIEL">Viel</option>
                            <option value="NORMAL">Normal</option>
                            <option value="WENIG">Wenig</option>
                        </select>
                    </div>

                    {details.kcalVerbrauch > 0 && (
                        <div className="text-sm text-muted-foreground">
                            Errechneter Verbrauch:{" "}
                            <span className="font-bold">{details.kcalVerbrauch} kcal</span>
                        </div>
                    )}
                </div>

                <DialogFooter>
                    <Button onClick={handleSave}>Speichern</Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}